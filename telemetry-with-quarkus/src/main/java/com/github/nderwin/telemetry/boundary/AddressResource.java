/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.boundary;

import com.github.nderwin.telemetry.control.ValidationClient;
import com.github.nderwin.telemetry.control.VerificationRequest;
import com.github.nderwin.telemetry.entity.Address;
import com.github.nderwin.telemetry.entity.Contact;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.BadRequestException;
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
import java.util.stream.Collectors;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Blocking
@Transactional(Transactional.TxType.REQUIRES_NEW)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("contacts/{contactId}/addresses")
@RequestScoped
public class AddressResource {

    @Inject
    UriInfo uriInfo;
    
    @Inject
    @RestClient
    ValidationClient client;
    
    @GET
    public Response list(
            @PathParam("contactId")
            final long contactId
    ) {
        final Contact c = Contact.findById(contactId);
        if (null == c) {
            throw new NotFoundException();
        }
        
        return Response
                .ok(c.getAddresses()
                        .stream()
                        .map((a) -> AddressRecord.of(a))
                        .collect(Collectors.toList()))
                .build();
    }
    
    @POST
    public Response create(
            @PathParam("contactId")
            final long contactId,
            
            final AddressRecord ar
    ) {
        final Contact c = Contact.findById(contactId);
        if (null == c) {
            throw new NotFoundException();
        }
        
        final Address a = new Address(
                ar.deliveryLine(), 
                ar.city(), 
                ar.territory(), 
                ar.postalCode()
        );
        
        c.addAddress(a);
        c.persistAndFlush();
        
        client.validate(new VerificationRequest(
                c.getId(),
                c.getAddresses().indexOf(a),
                a.getDeliveryLine(),
                a.getCity(),
                a.getTerritory(),
                a.getPostalCode()
        ));

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("{0}").build(c.getAddresses().indexOf(a)))
                .build();
    }
    
    @GET
    @Path("{id}")
    public Response read(
            @PathParam("contactId")
            final long contactId,

            @PositiveOrZero
            @PathParam("id")
            final int id
    ) {
        final Contact c = Contact.findById(contactId);
        if (null == c) {
            throw new NotFoundException();
        }
        
        try {
            final Address a = c.getAddresses().get(id);
            
            return Response
                    .ok(a)
                    .build();
        } catch (final IndexOutOfBoundsException ex) {
            throw new BadRequestException(ex);
        }
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("contactId")
            final long contactId,

            @PositiveOrZero
            @PathParam("id")
            final int id,

            final AddressRecord address
    ) {
        final Contact c = Contact.findById(contactId);
        if (null == c) {
            throw new NotFoundException();
        }
        
        try {
            final Address a = c.getAddresses().get(id);
            
            a.setCity(address.city());
            a.setDeliveryLine(address.deliveryLine());
            a.setPostalCode(address.postalCode());
            a.setTerritory(address.territory());
            a.setVerified(false);

            client.validate(new VerificationRequest(
                    c.getId(),
                    id,
                    a.getDeliveryLine(),
                    a.getCity(),
                    a.getTerritory(),
                    a.getPostalCode()
            ));

            return Response
                    .ok(a)
                    .build();
        } catch (final IndexOutOfBoundsException ex) {
            throw new BadRequestException(ex);
        }
    }
    
    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("contactId")
            final long contactId,
        
            @PositiveOrZero
            @PathParam("id")
            final int id
    ) {
        final Contact c = Contact.findById(contactId);
        if (null == c) {
            throw new NotFoundException();
        }


        try {
            final Address a = c.getAddresses().get(id);
            
            c.removeAddress(a);
        } catch (final IndexOutOfBoundsException ex) {
            throw new BadRequestException(ex);
        }
        
        return Response
                .noContent()
                .build();
    }
    
    public record AddressRecord(
            String deliveryLine,
            String city,
            String territory,
            String postalCode,
            boolean verified) {

        public static AddressRecord of(Address addr) {
            return new AddressRecord(
                    addr.getDeliveryLine(), 
                    addr.getCity(), 
                    addr.getTerritory(), 
                    addr.getPostalCode(),
                    addr.isVerified()
            );
        }
    }

}
