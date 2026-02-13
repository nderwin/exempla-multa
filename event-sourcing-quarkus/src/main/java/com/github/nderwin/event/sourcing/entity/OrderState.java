package com.github.nderwin.event.sourcing.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.github.nderwin.event.sourcing.entity.OrderStatus.DRAFT;
import static java.math.BigDecimal.ZERO;

public record OrderState(
        UUID orderId,
        String customerEmail,
        List<OrderLine> items,
        OrderStatus status,
        BigDecimal total) {

    public static OrderState initial(final UUID id, final String email) {
        return new OrderState(
                id,
                email,
                List.of(),
                DRAFT,
                ZERO
        );
    }
    
    public static OrderState empty() {
        return new OrderState(
                null,
                null,
                List.of(),
                DRAFT,
                ZERO
        );
    }
}
