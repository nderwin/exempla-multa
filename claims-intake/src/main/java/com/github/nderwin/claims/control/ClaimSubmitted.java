package com.github.nderwin.claims.control;

import java.math.BigDecimal;

public record ClaimSubmitted(
        String eventId,
        String claimId,
        String customerId,
        BigDecimal amount) {

}
