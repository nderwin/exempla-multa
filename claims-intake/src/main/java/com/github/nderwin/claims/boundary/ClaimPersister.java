package com.github.nderwin.claims.boundary;

import com.github.nderwin.claims.control.ClaimAccepted;
import com.github.nderwin.claims.control.ClaimValidated;
import com.github.nderwin.claims.entity.ProcessedEvent;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class ClaimPersister {
    
    @Incoming("enriched")
    @Outgoing("claims-accepted")
    @Transactional
    public ClaimAccepted persist(final ClaimValidated claim) {
        if (ProcessedEvent.alreadyProcessed(claim.eventId())) {
            Log.warnf(
                    "[DUPLICATE] eventId=%s, claimId=%s - Already processed, ignoring",
                    claim.eventId(),
                    claim.claimId()
            );
            
            return null;
        }
        
        ProcessedEvent.markProcessed(claim.eventId());
        Log.infof(
                "💾 [PERSISTED] eventId=%s, claimId=%s - Marked as processed",
                claim.eventId(),
                claim.claimId()
        );
        
        final ClaimAccepted accepted = new ClaimAccepted(
                claim.eventId(), 
                claim.claimId(), 
                "ACCEPTED"
        );
        
        Log.infof(
                "[ACCEPTED] eventId=%s, claimId=%s, status=ACCEPTED",
                claim.eventId(),
                claim.claimId()
        );
        
        return accepted;
    }
    
}
