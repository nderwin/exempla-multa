package com.github.nderwin.event.sourcing.control;

import com.github.nderwin.event.sourcing.control.Command.AddItem;
import com.github.nderwin.event.sourcing.control.Command.CancelOrder;
import com.github.nderwin.event.sourcing.control.Command.PlaceOrder;
import com.github.nderwin.event.sourcing.control.Command.ShipOrder;
import com.github.nderwin.event.sourcing.control.CommandResult.InvalidState;
import com.github.nderwin.event.sourcing.control.CommandResult.NotFound;
import com.github.nderwin.event.sourcing.control.CommandResult.Success;
import com.github.nderwin.event.sourcing.control.CommandResult.ValidationError;
import com.github.nderwin.event.sourcing.control.OrderEvent.ItemAdded;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderCancelled;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderPlaced;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderShipped;
import com.github.nderwin.event.sourcing.entity.OrderState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.github.nderwin.event.sourcing.entity.OrderStatus.DRAFT;
import static com.github.nderwin.event.sourcing.entity.OrderStatus.SHIPPED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class OrderCommandHandler {
    
    private static final String AGGREGATE_TYPE = "Order";
    
    @Inject
    EventStore store;
    
    @Transactional
    public CommandResult placeOrder(final PlaceOrder cmd) {
        if (null == cmd.customerEmail() || cmd.customerEmail().isBlank()) {
            return new ValidationError("Customer email must not be empty");
        }
        
        final UUID orderId = UUID.randomUUID();
        var event = new OrderPlaced(
                orderId,
                cmd.customerEmail(),
                Instant.now()
        );
        
        store.append(orderId, AGGREGATE_TYPE, event);
        return new Success(orderId);
    }

    @Transactional
    public CommandResult addItem(final AddItem cmd) {
        if (0 >= cmd.quantity()) {
            return new ValidationError("Quantity must be positive");
        }
        
        final OrderState current = loadCurrentState(cmd.orderId());
        if (null == current) {
            return new NotFound("Order not found: " + cmd.orderId());
        }
        
        if (DRAFT != current.status()) {
            return new InvalidState("Cannot add items to order in status " + current.status());
        }
        
        var event = new ItemAdded(
                cmd.orderId(),
                cmd.productName(),
                cmd.quantity(),
                cmd.price(),
                Instant.now()
        );
        store.append(cmd.orderId(), AGGREGATE_TYPE, event);
        return new Success(cmd.orderId());
    }

    @Transactional
    public CommandResult shipOrder(final ShipOrder cmd) {
        final OrderState current = loadCurrentState(cmd.orderId());
        if (null == current) {
            return new NotFound("Order not found: " + cmd.orderId());
        }
        
        if (DRAFT != current.status()) {
            return new InvalidState("Only DRAFT orders can be shipped.  Current status: " + current.status());
        }
        
        var event = new OrderShipped(
                cmd.orderId(),
                cmd.trackingNumber(),
                Instant.now()
        );
        store.append(cmd.orderId(), AGGREGATE_TYPE, event);
        return new Success(cmd.orderId());
    }
    
    @Transactional
    public CommandResult cancelOrder(final CancelOrder cmd) {
        final OrderState current = loadCurrentState(cmd.orderId());
        if (null == current) {
            return new NotFound("Order not found: " + cmd.orderId());
        }
        
        if (SHIPPED == current.status()) {
            return new InvalidState("Cannot cancel shipped order");
        }
        
        var event = new OrderCancelled(
                cmd.orderId(),
                cmd.reason(),
                Instant.now()
        );
        store.append(cmd.orderId(), AGGREGATE_TYPE, event);
        return new Success(cmd.orderId());
    }

    @Transactional(SUPPORTS)
    public OrderState loadCurrentState(final UUID orderId) {
        final List<OrderEvent> events = store.loadEvents(orderId);
        
        if (events.isEmpty()) {
            return null;
        }
        
        return EventProjection.replayEvents(events);
    }
    
}
