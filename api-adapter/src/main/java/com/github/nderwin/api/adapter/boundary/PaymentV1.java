package com.github.nderwin.api.adapter.boundary;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentV1(
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount) {

}
