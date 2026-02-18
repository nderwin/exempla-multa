package com.github.nderwin.event.sourcing.boundary;

import com.github.nderwin.event.sourcing.control.Command.AddItem;
import com.github.nderwin.event.sourcing.control.Command.CancelOrder;
import com.github.nderwin.event.sourcing.control.Command.PlaceOrder;
import com.github.nderwin.event.sourcing.control.Command.ShipOrder;
import com.github.nderwin.event.sourcing.control.CommandResult;
import com.github.nderwin.event.sourcing.control.CommandResult.InvalidState;
import com.github.nderwin.event.sourcing.control.CommandResult.NotFound;
import com.github.nderwin.event.sourcing.control.CommandResult.Success;
import com.github.nderwin.event.sourcing.control.CommandResult.ValidationError;
import com.github.nderwin.event.sourcing.control.EventStore;
import com.github.nderwin.event.sourcing.control.OrderCommandHandler;
import com.github.nderwin.event.sourcing.control.OrderEvent;
import com.github.nderwin.event.sourcing.entity.OrderReadModel;
import com.github.nderwin.event.sourcing.entity.OrderState;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

@Path("orders")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RequestScoped
public class OrderResource {

    @Inject
    OrderCommandHandler handler;
    
    @Inject
    EventStore store;
    
    @POST
    public Response placeOrder(final PlaceOrderRequest request) {
        var cmd = new PlaceOrder(request.customerEmail());
        final CommandResult result = handler.placeOrder(cmd);
        
        return switch (result) {
            case Success s -> Response.created(URI.create("/orders/" + s.aggregateId())).entity(s).build();
            case ValidationError v -> Response.status(BAD_REQUEST).entity(v).build();
            default -> Response.serverError().entity(result).build();
        };
    }
    
    @Path("{id}/items")
    @POST
    public Response addItem(
            @PathParam("id")
            final UUID orderId,
            final AddItemRequest request
    ) {
        var cmd = new AddItem(orderId, request.productName(), request.quantity(), request.price());
        final CommandResult result = handler.addItem(cmd);
        
        return switch (result) {
            case Success s -> Response.ok(s).build();
            case NotFound n -> Response.status(NOT_FOUND).entity(n).build();
            case InvalidState i -> Response.status(CONFLICT).entity(i).build();
            case ValidationError v -> Response.status(BAD_REQUEST).entity(v).build();
        };
    }
    
    @Path("{id}/ship")
    @POST
    public Response ship(
            @PathParam("id")
            final UUID orderId,
            ShipOrderRequest request
    ) {
        var cmd = new ShipOrder(orderId, request.trackingNumber());
        final CommandResult result = handler.shipOrder(cmd);
        
        return switch (result) {
            case Success s -> Response.ok(s).build();
            case NotFound n -> Response.status(NOT_FOUND).entity(n).build();
            case InvalidState i -> Response.status(CONFLICT).entity(i).build();
            default -> Response.status(BAD_REQUEST).entity(result).build();
        };
    }
    
    @Path("{id}/cancel")
    @POST
    public Response cancel(
            @PathParam("id")
            final UUID orderId,
            CancelOrderRequest request
    ) {
        var cmd = new CancelOrder(orderId, request.reason());
        final CommandResult result = handler.cancelOrder(cmd);

        return switch (result) {
            case Success s -> Response.ok(s).build();
            case NotFound n -> Response.status(NOT_FOUND).entity(n).build();
            case InvalidState i -> Response.status(CONFLICT).entity(i).build();
            default -> Response.status(BAD_REQUEST).entity(result).build();
        };
    }

    @Path("{id}")
    @GET
    public Response getState(
            @PathParam("id")
            final UUID orderId
    ) {
        final OrderState state = handler.loadCurrentState(orderId);
        if (null == state) {
            return Response.status(NOT_FOUND).build();
        }
        
        return Response.ok(state).build();
    }
    
    @Path("{id}/events")
    @GET
    public List<OrderEvent> getEvents(
            @PathParam("id")
            final UUID orderId
    ) {
        return store.loadEvents(orderId);
    }
    
    @Path("{id}/read-model")
    @GET
    public Response getReadModel(
            @PathParam("id")
            final UUID orderId
    ) {
        final OrderReadModel model = OrderReadModel.findByOrderId(orderId);
        if (null == model) {
            return Response.status(NOT_FOUND).build();
        }
        
        return Response.ok(model).build();
    }
    
    public record PlaceOrderRequest(String customerEmail) {
        
    }
    
    public record AddItemRequest(String productName, int quantity, BigDecimal price) {
        
    }

    public record ShipOrderRequest(String trackingNumber) {
        
    }

    public record CancelOrderRequest(String reason) {
        
    }
    
}
