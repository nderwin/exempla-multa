package com.github.nderwin.audit.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.envers.Audited;

import static com.github.nderwin.audit.entity.CreditAccount.Status.ACTIVE;
import static com.github.nderwin.audit.entity.CreditAccount.Status.SUSPENDED;

@Entity
@Audited
public class CreditAccount extends PanacheEntity {
    
    private String owner;
    
    private long creditLimit;
    
    @Enumerated(EnumType.STRING)
    private Status status = ACTIVE;

    protected CreditAccount() {
    }

    public CreditAccount(final String owner, final long creditLimit) {
        this.owner = owner;
        this.creditLimit = creditLimit;
    }

    public String getOwner() {
        return owner;
    }

    public long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(final long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Status getStatus() {
        return status;
    }
    
    public void suspend() {
        this.status = SUSPENDED;
    }
    
    public enum Status {
        ACTIVE,
        SUSPENDED
    }

}
