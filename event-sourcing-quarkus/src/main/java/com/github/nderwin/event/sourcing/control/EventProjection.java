package com.github.nderwin.event.sourcing.control;

import com.github.nderwin.event.sourcing.control.OrderEvent.ItemAdded;
import com.github.nderwin.event.sourcing.control.OrderEvent.ItemRemoved;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderCancelled;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderPlaced;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderShipped;
import com.github.nderwin.event.sourcing.entity.OrderLine;
import com.github.nderwin.event.sourcing.entity.OrderState;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.github.nderwin.event.sourcing.entity.OrderState.empty;
import static com.github.nderwin.event.sourcing.entity.OrderStatus.CANCELLED;
import static com.github.nderwin.event.sourcing.entity.OrderStatus.SHIPPED;

public final class EventProjection {

    private EventProjection() {
    }
    
    public static OrderState apply(final OrderState state, final OrderEvent event) {
        return switch(event) {
            case OrderPlaced e -> OrderState.initial(e.orderId(), e.customerEmail());
            case ItemAdded e -> {
                var items = new ArrayList<>(state.items());
                items.add(new OrderLine(e.productName(), e.quantity(), e.price()));
                
                var newTotal = state.total().add(e.price().multiply(BigDecimal.valueOf(e.quantity())));
                
                yield new OrderState(
                        state.orderId(),
                        state.customerEmail(),
                        List.copyOf(items),
                        state.status(),
                        newTotal
                );
            }
            case ItemRemoved e -> {
                var items = new ArrayList<>(state.items());
                
                items.removeIf(line -> line.productName().equals(e.productName()) && line.price().compareTo(e.price()) == 0);
                
                var newTotal = state.total().subtract(e.price().multiply(BigDecimal.valueOf(e.quantity())));
                
                yield new OrderState(
                        state.orderId(),
                        state.customerEmail(),
                        List.copyOf(items),
                        state.status(),
                        newTotal
                );
            }
            case OrderCancelled e -> new OrderState(
                    state.orderId(),
                    state.customerEmail(),
                    state.items(),
                    CANCELLED,
                    state.total()
            );
            case OrderShipped e -> new OrderState(
                    state.orderId(),
                    state.customerEmail(),
                    state.items(),
                    SHIPPED,
                    state.total()
            );
        };
    }

    public static OrderState replayEvents(final List<OrderEvent> events) {
        return events.stream()
                .reduce(empty(), EventProjection::apply, (left, right) -> right);
    }
    
}
