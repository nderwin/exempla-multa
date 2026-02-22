package com.github.nderwin.claims.boundary;

import com.github.nderwin.claims.control.ClaimSubmitted;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("claims")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RequestScoped
public class ClaimResource {
    
    @Channel("claims-submitted")
    Emitter<ClaimSubmitted> emitter;
    
    @POST
    public Response submit(final ClaimSubmitted claim) {
        Log.infof(
                "[SUBMITTED] event=%s, claimId=%s, customerId=%s, amount=%.2f",
                claim.eventId(),
                claim.claimId(),
                claim.customerId(),
                claim.amount()
        );
        
        emitter.send(claim);
        return Response.accepted(claim).build();
    }
    
}
