package com.github.nderwin.bookstore.boundary;

import com.github.nderwin.bookstore.entity.Author;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CREATED;

@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("authors")
@RequestScoped
public class AuthorResource {
    
    @Inject
    Author.Repo repo;
    
    @GET
    public List<Author> list() {
        return repo.findAll().list();
    }
    
    @Path("country/{country}")
    @GET
    public List<Author> byCountry(
            @PathParam("country")
            final String country
    ) {
        return repo.findByCountry(country);
    }
    
    @Transactional
    @POST
    public Response create(final Author author) {
        repo.persist(author);
        return Response.status(CREATED).entity(author).build();
    }
    
    @Transactional
    @Path("{id}")
    @DELETE
    public void delete(
            @PathParam("id")
            final long id
    ) {
        repo.deleteById(id);
    }
    
}
