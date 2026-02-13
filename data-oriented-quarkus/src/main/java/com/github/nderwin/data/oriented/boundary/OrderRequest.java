package com.github.nderwin.data.oriented.boundary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderRequest(
        @NotBlank
        String customerEmail,
        @NotBlank
        String productName,
        @PositiveOrZero
        int quantity
        ) {

}
