package com.github.nderwin.api.adapter.boundary;

import com.github.nderwin.api.adapter.control.RequestAdapter;
import com.github.nderwin.api.adapter.control.ResponseAdapter;
import com.github.nderwin.api.adapter.entity.CanonicalPayment;
import com.github.nderwin.api.adapter.entity.PaymentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentV1Adapter {
    
    private static final String VERSION = "2023-10-16";

    @ApplicationScoped
    @Produces
    public RequestAdapter<PaymentV1> request() {
        return new RequestAdapter<PaymentV1>() {
            @Override
            public String version() {
                return VERSION;
            }

            @Override
            public Class<PaymentV1> requestType() {
                return PaymentV1.class;
            }

            @Override
            public CanonicalPayment toCanonical(final PaymentV1 request) {
                return new CanonicalPayment(
                        null, 
                        request.amount(), 
                        "CARD", 
                        PaymentStatus.PENDING, 
                        OffsetDateTime.now()
                );
            }
        };
    }
    
    @ApplicationScoped
    @Produces
    public ResponseAdapter<PaymentV1> response() {
        return new ResponseAdapter<PaymentV1>() {
            @Override
            public String version() {
                return VERSION;
            }

            @Override
            public Class<PaymentV1> responseType() {
                return PaymentV1.class;
            }

            @Override
            public PaymentV1 fromCanonical(final CanonicalPayment model) {
                final BigDecimal amt = model.getAmount() == null ? BigDecimal.ZERO : model.getAmount();
                
                return new PaymentV1(amt);
            }
        };
    }
    
}
