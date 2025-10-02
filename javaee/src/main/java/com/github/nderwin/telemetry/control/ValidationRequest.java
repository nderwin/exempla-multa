/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.control;

public class ValidationRequest {

    private long contactId;

    private int position;

    private String deliveryLine;

    private String city;

    private String territory;

    private String postalCode;

    public ValidationRequest() {
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(final long contactId) {
        this.contactId = contactId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        this.position = position;
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

}
