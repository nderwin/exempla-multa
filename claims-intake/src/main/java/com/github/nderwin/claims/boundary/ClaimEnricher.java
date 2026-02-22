package com.github.nderwin.claims.boundary;

import com.github.nderwin.claims.control.ClaimValidated;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class ClaimEnricher {
    
    private static final int MAX_ATTEMPTS = 3;
    
    @Incoming("validated")
    @Outgoing("claims-enriched")
    public ClaimValidated enrich(final ClaimValidated claim) {
        Log.infof(
                "[ENRICHING] eventId=%s, claimId=%s - Calling external service...", 
                claim.eventId(),
                claim.claimId()
        );
        
        int attempt = 0;
        
        while (true) {
            try {
                attempt++;
                
                if (attempt > 1) {
                    Log.infof(
                            "[ENRICHING RETRY] eventId=%s, claimId=%s - Attempt %d/%d",
                            claim.eventId(),
                            claim.claimId(),
                            attempt,
                            MAX_ATTEMPTS
                    );
                }
                
                callExternalService(claim);
                
                Log.infof(
                        "✨ [ENRICHED] eventId=%s, claimId=%s", 
                        claim.eventId(),
                        claim.claimId()
                );
                
                return claim;
            } catch (final RuntimeException ex) {
                if (attempt >= MAX_ATTEMPTS) {
                    Log.errorf(
                            "[ENRICHMENT FAILED] eventId=%s, claimId=%s - Max attempts reached, routing to DLQ",
                            claim.eventId(),
                            claim.claimId()
                    );
                    
                    throw ex;
                }
                
                Log.warnf(
                        "[ENRICHMENT RETRY] eventId=%s, claimId=%s - External service timeout, retrying...",
                        claim.eventId(),
                        claim.claimId()
                );
                
                backoff(attempt);
            }
        }
    }

    private void callExternalService(final ClaimValidated claim) {
        if (ThreadLocalRandom.current().nextInt(4) == 0) {
            throw new RuntimeException("External service timeout");
        }
    }
    
    private void backoff(final int attempt) {
        try {
            Thread.sleep(500L * attempt);
        } catch (final InterruptedException ignored) {
            
        }
    }
    
}
