/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import org.hibernate.annotations.Parent;

@Embeddable
public class Address extends PanacheEntityBase {

    @JsonbTransient
    @Parent
    private Contact contact;
    
    @NotNull
    @Column(name = "deliveryline", length = 64, nullable = false)
    private String deliveryLine;

    @NotNull
    @Column(name = "city", length = 64, nullable = false)
    private String city;

    @Column(name = "territory", length = 2)
    private String territory;

    @Column(name = "postalcode", length = 9)
    private String postalCode;

    @Column(name = "verified", nullable = false)
    private boolean verified;
    
    protected Address() {
    }
    
    public Address(final String deliveryLine, final String city, final String territory, final String postalCode) {
        this.deliveryLine = deliveryLine;
        this.city = city;
        this.territory = territory;
        this.postalCode = postalCode;
        this.verified = false;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(final Contact contact) {
        this.contact = contact;
    }

    public String getDeliveryLine() {
        return deliveryLine;
    }

    public void setDeliveryLine(final String deliveryLine) {
        this.deliveryLine = deliveryLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(final String territory) {
        this.territory = territory;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(final boolean verified) {
        this.verified = verified;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.deliveryLine);
        hash = 89 * hash + Objects.hashCode(this.city);
        hash = 89 * hash + Objects.hashCode(this.territory);
        hash = 89 * hash + Objects.hashCode(this.postalCode);
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
        final Address other = (Address) obj;
        if (!Objects.equals(this.deliveryLine, other.deliveryLine)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (!Objects.equals(this.territory, other.territory)) {
            return false;
        }
        return Objects.equals(this.postalCode, other.postalCode);
    }

}
