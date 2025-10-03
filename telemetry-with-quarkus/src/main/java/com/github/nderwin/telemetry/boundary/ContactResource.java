/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.boundary;

import com.github.nderwin.telemetry.entity.Contact;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

@Blocking
@Transactional(REQUIRES_NEW)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("contacts")
@RequestScoped
public class ContactResource {

    @Inject
    UriInfo uriInfo;
    
    @GET
    public Response list() {
        return Response
                .ok(Contact.listAll())
                .build();
    }

    @POST
    public Response create(final ContactRecord contact) {
        final Contact c = new Contact(contact.name());
        c.persistAndFlush();

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("{0}").build(c.getId()))
                .build();
    }

    @GET
    @Path("{id}")
    public Response read(
            @PathParam("id")
            final long id
    ) {
        final Contact c = Contact.findById(id);
        if (null == c) {
            throw new NotFoundException();
        }

        return Response
                .ok(c)
                .build();
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("id")
            final long id,
            final ContactRecord contact
    ) {
        final Contact c = Contact.findById(id);
        if (null == c) {
            throw new NotFoundException();
        }

        c.setName(contact.name());

        return Response
                .ok(c)
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("id")
            final long id
    ) {
        final Contact c = Contact.findById(id);
        if (null != c) {
            c.delete();
        }
        
        return Response
                .noContent()
                .build();
    }
    
    public record ContactRecord(
            String name) {

    }

}
