package com.github.nderwin.audit.boundary;

import com.github.nderwin.audit.control.CreditAccountAuditService;
import com.github.nderwin.audit.control.CreditAccountAuditService.AuditSnapshot;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("accounts/{id}/audit")
@Produces(APPLICATION_JSON)
@RequestScoped
public class CreditAccountAuditResource {
    
    @Inject
    CreditAccountAuditService service;
    
    @GET
    @Path("revisions")
    public List<Number> revisions(
            @PathParam("id")
            final long accountId
    ) {
        return service.revisions(accountId);
    }
    
    @GET
    @Path("revisions/{revision}")
    public AuditSnapshot revision(
            @PathParam("id")
            final long accountId,
            @PathParam("revision")
            final long revision
    ) {
        final AuditSnapshot snapshot = service.snapshot(accountId, revision);
        
        if (null == snapshot) {
            throw new NotFoundException("No revision " + revision + " for account " + accountId);
        }
        
        return snapshot;
    }
    
}
