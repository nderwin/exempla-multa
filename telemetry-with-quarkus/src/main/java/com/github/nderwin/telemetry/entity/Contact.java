/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Table(schema = "demo", name = "contact")
@Entity
public class Contact extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_sequence")
    @SequenceGenerator(name = "contact_sequence", schema = "demo", sequenceName = "contact_seq", initialValue = 1, allocationSize = 50)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @NotNull
    @Column(name = "name", nullable = false, length = 512)
    private String name;

    @JsonbTransient
    @ElementCollection
    @CollectionTable(schema = "demo", name = "address", joinColumns = @JoinColumn(name = "contactid"))
    @OrderColumn(name = "position", nullable = false)
    private List<Address> addresses = new ArrayList<>();

    protected Contact() {
    }

    public Contact(final String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    public void setAddresses(final List<Address> addresses) {
        this.addresses.clear();
        
        if (null != addresses) {
            this.addresses.addAll(addresses);
        }
    }
    
    public boolean addAddress(final Address address) {
        return this.addresses.add(address);
    }
    
    public boolean removeAddress(final Address address) {
        return this.addresses.remove(address);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.name);
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
        final Contact other = (Contact) obj;
        return Objects.equals(this.name, other.name);
    }
    
}
