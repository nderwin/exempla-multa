/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.boundary;

import com.github.nderwin.telemetry.control.ValidationRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("validations")
@Stateless
public class ValidationResource {

    private static final Logger LOG = Logger.getLogger(ValidationResource.class.getName());
    
    @Inject
    Event<ValidationRequest> event;
    
    @PersistenceContext
    EntityManager em;
    
    @POST
    public Response validate(
            final ValidationRequest request
    ) {
        final Object obj = em.createNativeQuery("SELECT CURRENT_DATE").getSingleResult();
        
        LOG.log(Level.INFO, "It is now: {0}", obj);

        // Fake a long running query, but really, we shouldn't do this in an EE application
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        
        event.fire(request);

        return Response
                .status(Response.Status.ACCEPTED)
                .build();
    }

}
