package com.github.nderwin.claims.control;

public record ClaimAccepted(
        String eventId,
        String claimId,
        String status) {

}
