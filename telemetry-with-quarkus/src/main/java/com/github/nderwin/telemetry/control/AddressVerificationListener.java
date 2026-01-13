/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.control;

import com.github.nderwin.telemetry.entity.Address;
import com.github.nderwin.telemetry.entity.Contact;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.common.annotation.Blocking;
import io.vertx.core.json.Json;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.logging.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import static jakarta.enterprise.event.TransactionPhase.AFTER_SUCCESS;

public class AddressVerificationListener {

    private static final Logger LOG = Logger.getLogger(AddressVerificationListener.class.getName());

    @Inject
    @RestClient
    ValidationClient client;
    
    public void onVerificationRequest(@Observes(during = AFTER_SUCCESS) VerificationRequest req) {
        client.validate(req);
    }
    
    @WithSpan(kind = SpanKind.CONSUMER)
    @Blocking
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Incoming("address-verify")
    public void consume(final byte[] payload) {
        try {
            final String text = new String(payload);
            LOG.info("payload: " + text);
        
            final VerificationRecord vr = Json.decodeValue(text, VerificationRecord.class);

            final Contact c = Contact.findById(vr.contactId());
            final Address a = c.getAddresses().get(vr.addressId());
            
            a.setVerified(vr.verified());
        } catch (final Throwable t) {
            LOG.warning(t.getMessage());
        }
    }

    public record VerificationRecord(
            long contactId,
            int addressId,
            boolean verified) {

    }

}
