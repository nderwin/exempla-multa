package com.github.nderwin.workflow.control;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {
    
    @WorkflowMethod
    void processOrder(String orderId);
    
}
