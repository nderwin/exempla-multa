package com.github.nderwin.event.sourcing.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orderreadmodel")
public class OrderReadModel extends PanacheEntity {
    
    @Column(nullable = false, columnDefinition = "uuid", unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private Instant lastUpdated;

    protected OrderReadModel() {
    }

    public OrderReadModel(final UUID orderId) {
        this.orderId = orderId;
    }
    
    @PrePersist
    @PreUpdate
    void onChange() {
        this.lastUpdated = Instant.now();
    }

    public static OrderReadModel findByOrderId(final UUID orderId) {
        return find("orderId", orderId).firstResult();
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(final BigDecimal total) {
        this.total = total;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

}
