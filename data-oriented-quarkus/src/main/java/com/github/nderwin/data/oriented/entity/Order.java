package com.github.nderwin.data.oriented.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order")
public class Order extends PanacheEntity {

    @NotBlank
    private String customerEmail;
    
    private String productName;
    
    @PositiveOrZero
    private int quantity = 0;
    
    @NotNull
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    private String status;
    
    private Instant createdAt;

    protected Order() {
    }

    public Order(final String customerEmail, final String productName, final int quantity, final BigDecimal totalAmount) {
        this.customerEmail = customerEmail;
        this.productName = productName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }

}
