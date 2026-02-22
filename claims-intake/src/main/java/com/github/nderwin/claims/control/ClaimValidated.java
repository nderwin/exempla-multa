package com.github.nderwin.claims.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record ClaimValidated(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("claimId") String claimId,
        @JsonProperty("customerId") String customerId,
        @JsonProperty("amount") BigDecimal amount) {

}
