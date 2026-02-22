package com.github.nderwin.workflow.control;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class DefaultOrderWorkflow implements OrderWorkflow {

    private final OrderActivityStep activities = Workflow.newActivityStub(
            OrderActivityStep.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(3)
                            .build()
                    )
                    .build()
    );
    
    @Override
    public void processOrder(final String orderId) {
        try {
            activities.reserveInventory(orderId);
            activities.chargePayment(orderId);
            
            final int version = Workflow.getVersion("order-confirmation", Workflow.DEFAULT_VERSION, 1);
            if (version == 1) {
                activities.confirmOrder(orderId);
            }
        } catch (final Exception ex) {
            Workflow.getLogger(this.getClass()).error("Order failed, compensating", ex);
            
            compensate(orderId);
            
            throw ex;
        }
    }

    private void compensate(final String orderId) {
        activities.refundPayment(orderId);
        activities.releaseInventory(orderId);
    }
    
}
