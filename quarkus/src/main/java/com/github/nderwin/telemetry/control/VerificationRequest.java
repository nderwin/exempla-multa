/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.control;

public record VerificationRequest(
        long contactId,
        int position,
        String deliveryLine,
        String city,
        String territory,
        String postalCode) {

}
