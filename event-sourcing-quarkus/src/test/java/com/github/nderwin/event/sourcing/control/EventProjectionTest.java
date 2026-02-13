package com.github.nderwin.event.sourcing.control;

import com.github.nderwin.event.sourcing.control.OrderEvent.ItemAdded;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderPlaced;
import com.github.nderwin.event.sourcing.control.OrderEvent.OrderShipped;
import com.github.nderwin.event.sourcing.entity.OrderState;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static com.github.nderwin.event.sourcing.entity.OrderStatus.SHIPPED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventProjectionTest {

    @Test
    public void testOrderLifecycle() {
        final UUID orderId = UUID.randomUUID();
        final Instant now = Instant.now();
        final BigDecimal price = new BigDecimal("49.95");
        
        final List<OrderEvent> events = List.of(
                new OrderPlaced(orderId, "test@example.com", now),
                new ItemAdded(orderId, "Laptop", 1, price, now),
                new OrderShipped(orderId, "TRACK-123", now)
        );
        
        final OrderState state = EventProjection.replayEvents(events);
        
        assertEquals(orderId, state.orderId());
        assertEquals("test@example.com", state.customerEmail());
        assertEquals(SHIPPED, state.status());
        assertEquals(price, state.total());
    }
    
}
