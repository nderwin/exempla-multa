/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.entity;

import com.github.nderwin.telemetry.control.ValidationClient;
import com.github.nderwin.telemetry.control.VerificationRequest;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import java.util.logging.Logger;

public class ContactEntityListener {

    private static final Logger LOG = Logger.getLogger(ContactEntityListener.class.getName());

    private final Instance<ValidationClient> client;

    public ContactEntityListener() {
        client = CDI.current().select(ValidationClient.class);
    }

//    @WithSpan(kind = SpanKind.CONSUMER)
    @PostPersist
    @PostUpdate
    public void onPersistOrUpdate(final Contact con) {
        if (client.isResolvable()) {
            for (int i = 0; i < con.getAddresses().size(); i++) {
                final Address addr = con.getAddresses().get(i);
                if (!addr.isVerified()) {
                    client.get().validate(new VerificationRequest(
                            con.getId(),
                            i,
                            addr.getDeliveryLine(),
                            addr.getCity(),
                            addr.getTerritory(),
                            addr.getPostalCode()
                    ));
                }
            }
        } else {
            LOG.warning("No client found for verifying address");
        }
    }

}
