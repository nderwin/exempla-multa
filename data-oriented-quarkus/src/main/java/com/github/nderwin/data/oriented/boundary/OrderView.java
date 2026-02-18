package com.github.nderwin.data.oriented.boundary;

import java.math.BigDecimal;

public record OrderView(
        Long id,
        String customerEmail,
        String productName,
        int quantity,
        BigDecimal totalAmount,
        String status,
        String createdAt) {

}
