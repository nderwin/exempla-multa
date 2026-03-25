package com.github.nderwin.audit.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class AccountHolder {
    
    private String name;

    protected AccountHolder() {
    }

    public AccountHolder(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccountHolder other = (AccountHolder) obj;
        return Objects.equals(this.name, other.name);
    }
    
}
