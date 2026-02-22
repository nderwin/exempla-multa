package com.github.nderwin.claims.boundary;

import com.github.nderwin.claims.control.ClaimSubmitted;
import com.github.nderwin.claims.control.ClaimValidated;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class ClaimValidator {
    
    @Incoming("submitted")
    @Outgoing("claims-validated")
    public ClaimValidated validate(final ClaimSubmitted claim) {
        if (BigDecimal.ZERO.compareTo(claim.amount()) != -1) {
            Log.errorf(
                    "[VALIDATION FAILED] eventId=%s, claimId=%s - Amount must be positive (amount=%.2f)",
                    claim.eventId(),
                    claim.claimId(),
                    claim.amount()
            );
            
            throw new IllegalArgumentException("Claim amount must be positive");
        }
        
        Log.infof(
                "[VALIDATED] eventId=%s, claimId=%s, amount=%.2f",
                claim.eventId(),
                claim.claimId(),
                claim.amount()
        );
        
        return new ClaimValidated(
                claim.eventId(), 
                claim.claimId(), 
                claim.customerId(), 
                claim.amount()
        );
    }
    
}
