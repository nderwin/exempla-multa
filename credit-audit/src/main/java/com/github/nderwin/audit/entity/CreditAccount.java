package com.github.nderwin.audit.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    
    @ElementCollection
    List<AccountHolder> accountHolders = new ArrayList<>();
    
    // TODO - other types of collections

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

    public List<AccountHolder> getAccountHolders() {
        return Collections.unmodifiableList(accountHolders);
    }
    
    public boolean addAccountHolder(final AccountHolder holder) {
        if (null == holder) {
            throw new IllegalArgumentException("Account holder must not be null");
        }
        
        return accountHolders.add(holder);
    }
    
    public enum Status {
        ACTIVE,
        SUSPENDED
    }

}
