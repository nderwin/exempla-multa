package com.github.nderwin.api.adapter.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CanonicalPayment {

    private String id;
    
    private BigDecimal amount;
    
    private String method;
    
    private PaymentStatus status;
    
    private OffsetDateTime createdAt;

    protected CanonicalPayment() {
    }

    public CanonicalPayment(
            final String id, 
            final BigDecimal amount, 
            final String method, 
            final PaymentStatus status, 
            final OffsetDateTime createdAt
    ) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
}
