package com.github.nderwin.data.oriented.boundary;

import java.math.BigDecimal;

public sealed interface OrderResult permits OrderResult.Success, OrderResult.OutOfStock, OrderResult.ProductNotFound {

    record Success(
            Long orderId,
            String customerEmail,
            String productName,
            int quantity,
            BigDecimal totalAmount) implements OrderResult {

    }

    record OutOfStock(
            String productName,
            int available,
            int requested) implements OrderResult {

    }

    record ProductNotFound(
            String productName) implements OrderResult {

    }

}
