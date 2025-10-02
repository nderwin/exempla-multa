/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.control;

import com.github.nderwin.telemetry.entity.Address;
import com.github.nderwin.telemetry.entity.Contact;
import io.smallrye.common.annotation.Blocking;
import io.vertx.core.json.Json;
import jakarta.transaction.Transactional;
import java.util.logging.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;

public class AddressVerificationListener {

    private static final Logger LOG = Logger.getLogger(AddressVerificationListener.class.getName());

//    @WithSpan(kind = SpanKind.CONSUMER)
    @Blocking
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Incoming("address-verify")
    public void consume(final String payload) {
        try {
            final VerificationRecord vr = Json.decodeValue(payload, VerificationRecord.class);

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
