package com.github.nderwin.api.adapter.control;

import com.github.nderwin.api.adapter.entity.CanonicalPayment;
import com.github.nderwin.api.adapter.entity.PaymentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;
import java.util.UUID;

@ApplicationScoped
public class PaymentService {

    public CanonicalPayment create(final CanonicalPayment request) {
        final String id = UUID.randomUUID().toString();

        return new CanonicalPayment(
                id,
                request.getAmount(),
                null == request.getMethod() ? "CARD" : request.getMethod(),
                PaymentStatus.AUTHORIZED,
                OffsetDateTime.now()
        );
    }

}
