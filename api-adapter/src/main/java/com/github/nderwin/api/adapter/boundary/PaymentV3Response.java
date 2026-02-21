package com.github.nderwin.api.adapter.boundary;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentV3Response(
        String id,
        BigDecimal amount,
        String method,
        String status,
        OffsetDateTime createdAt,
        boolean confirmationRequired
        ) {

}
