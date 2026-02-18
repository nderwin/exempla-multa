package com.github.nderwin.data.oriented.boundary;

import com.github.nderwin.data.oriented.boundary.OrderResult.OutOfStock;
import com.github.nderwin.data.oriented.boundary.OrderResult.ProductNotFound;
import com.github.nderwin.data.oriented.boundary.OrderResult.Success;
import com.github.nderwin.data.oriented.control.OrderOperations;
import io.quarkiverse.resteasy.problem.validation.HttpValidationProblem;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

@Path("orders")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class OrderResource {

    @GET
    public List<OrderView> listOrders() {
        return OrderOperations.getAllOrders();
    }
    
    @APIResponses(value = {
        @APIResponse(
                responseCode = "201", 
                description = "Order created successfully",
                content = @Content(
                        mediaType = APPLICATION_JSON,
                        schema = @Schema(implementation = Success.class)
                )
        ),
        @APIResponse(
                responseCode = "400", 
                description = "Invalid request, usually due to validation failure",
                content = {
                    @Content(
                        mediaType = "application/problem+json",
                        schema = @Schema(implementation = HttpValidationProblem.class)
                    )
                }
        ),
        @APIResponse(
                responseCode = "404",
                description = "Requested product was not found",
                content = @Content(
                        mediaType = APPLICATION_JSON,
                        schema = @Schema(implementation = ProductNotFound.class)
                )
        ),
        @APIResponse(
                responseCode = "409",
                description = "Not enough product on hand to satisfy the request",
                content = @Content(
                        mediaType = APPLICATION_JSON,
                        schema = @Schema(implementation = OutOfStock.class)
                )
        )
    })
    @POST
    @Transactional
    public Response placeOrder(@Valid final OrderRequest request) {
        return switch (OrderOperations.placeOrder(request)) {
            case Success success -> Response.status(CREATED).entity(success).build();
            case OutOfStock outOfStock -> Response.status(CONFLICT).entity(outOfStock).build();
            case ProductNotFound notFound -> Response.status(NOT_FOUND).entity(notFound).build();
        };
    }
}
