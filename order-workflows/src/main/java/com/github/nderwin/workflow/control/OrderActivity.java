package com.github.nderwin.workflow.control;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * In real systems, this is where REST calls, messaging, or database access happens
 */
@ApplicationScoped
public class OrderActivity implements OrderActivityStep {

    @Override
    public void reserveInventory(final String orderId) {
        Log.infof("Inventory reserved for %s", orderId);
    }

    @Override
    public void chargePayment(final String orderId) {
        Log.infof("Payment charged for %s", orderId);
    }

    @Override
    public void confirmOrder(final String orderId) {
        Log.infof("Order confirmed for %s", orderId);
    }

    @Override
    public void releaseInventory(final String orderId) {
        Log.infof("Inventory released for %s", orderId);
    }

    @Override
    public void refundPayment(final String orderId) {
        Log.infof("Payment refunded for %s", orderId);
    }
    
}
