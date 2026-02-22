package com.github.nderwin.workflow.boundary;

import com.github.nderwin.workflow.control.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("orders")
@RequestScoped
public class OrderResource {

    @Inject
    WorkflowClient workflowClient;

    @POST
    @Path("{id}")
    public void start(
            @PathParam("id")
            final String id
    ) {
        final OrderWorkflow workflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("OrderWorkflow")
                        .build()
        );
        
        WorkflowClient.start(workflow::processOrder, id);
    }
}
