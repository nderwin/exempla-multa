package com.github.nderwin.api.adapter.boundary;

import com.github.nderwin.api.adapter.control.RequestAdapter;
import com.github.nderwin.api.adapter.control.ResponseAdapter;
import com.github.nderwin.api.adapter.entity.CanonicalPayment;
import com.github.nderwin.api.adapter.entity.PaymentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentV3Adapter {
    
    private static final String VERSION = "2024-09-01";

    @ApplicationScoped
    @Produces
    public RequestAdapter<PaymentV2> request() {
        return new RequestAdapter<PaymentV2>() {
            @Override
            public String version() {
                return VERSION;
            }

            @Override
            public Class<PaymentV2> requestType() {
                return PaymentV2.class;
            }

            @Override
            public CanonicalPayment toCanonical(final PaymentV2 request) {
                return new CanonicalPayment(
                        null, 
                        request.amount(), 
                        request.method(), 
                        PaymentStatus.PENDING, 
                        OffsetDateTime.now()
                );
            }
        };
    }
    
    @ApplicationScoped
    @Produces
    public ResponseAdapter<PaymentV3Response> response() {
        return new ResponseAdapter<PaymentV3Response>() {
            @Override
            public String version() {
                return VERSION;
            }

            @Override
            public Class<PaymentV3Response> responseType() {
                return PaymentV3Response.class;
            }

            @Override
            public PaymentV3Response fromCanonical(final CanonicalPayment model) {
                final boolean confirmationRequired = model.getStatus() == PaymentStatus.AUTHORIZED;
                final BigDecimal amt = model.getAmount() == null ? BigDecimal.ZERO : model.getAmount();
                
                return new PaymentV3Response(
                        model.getId(), 
                        amt, 
                        model.getMethod(), 
                        model.getStatus().name(), 
                        model.getCreatedAt(), 
                        confirmationRequired
                );
            }
        };
    }

}
