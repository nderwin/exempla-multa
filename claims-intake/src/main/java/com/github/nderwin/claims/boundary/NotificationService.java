package com.github.nderwin.claims.boundary;

import com.github.nderwin.claims.control.ClaimAccepted;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class NotificationService {
    
    @Incoming("accepted")
    public void notify(final ClaimAccepted claim) {
        Log.infof(
                "[NOTIFICATION] eventId=%s, claimId=%s, status=%s - Sending notification to customer",
                claim.eventId(),
                claim.claimId(),
                claim.status()
        );
    }
}
