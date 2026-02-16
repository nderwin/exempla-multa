package com.github.nderwin.audit.boundary;

import com.github.nderwin.audit.control.CreditAccountService;
import com.github.nderwin.audit.entity.CreditAccount;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("accounts")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RequestScoped
public class CreditAccountResource {
    
    @Inject
    CreditAccountService service;
    
    @POST
    public CreditAccount create(final CreateAccountRequest request) {
        return service.create(request.owner(), request.limit());
    }
    
    @PUT
    @Path("{id}/limit")
    public CreditAccount updateLimit(
            @PathParam("id")
            final long id,
            final UpdateLimitRequest request
    ) {
        return service.updateLimit(id, request.limit());
    }
    
    @POST
    @Path("{id}/suspend")
    public CreditAccount suspend(
            @PathParam("id")
            final long id
    ) {
        return service.suspend(id);
    }
    
    public static record CreateAccountRequest(String owner, long limit) {
        
    }
    
    public static record UpdateLimitRequest(long limit) {
        
    }
    
}
