package com.github.nderwin.bookstore.boundary;

import com.github.nderwin.bookstore.entity.Book;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;

import static io.quarkus.hibernate.reactive.panache.common.runtime.SessionOperations.withStatelessTransaction;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CREATED;

@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("books")
public class BookResource {
    
    @Inject
    Book.Repo repo;
    
    @GET
    public Uni<List<Book>> list() {
        return withStatelessTransaction(() -> repo.findAll().list());
    }
    
    @Path("since/{year}")
    @GET
    public Uni<List<Book>> since(
            @PathParam("year")
            final int year
    ) {
        return withStatelessTransaction(() -> repo.findPublishedSince(year));
    }
    
    @POST
    public Uni<Response> create(final Book book) {
        return withStatelessTransaction(() -> repo.insert(book).map(b -> Response.status(CREATED).entity(b).build()));
    }
    
    @Path("{id}")
    @PUT
    public Uni<Book> update(
            @PathParam("id")
            final long id,
            final Book patch
    ) {
        return withStatelessTransaction(() -> {
            final Book existing = repo.findById(id).await().indefinitely();
            
            if (null == existing) {
                return Uni.createFrom().failure(new NotFoundException());
            }
            
            existing.setTitle(patch.getTitle());
            existing.setYear(patch.getYear());
            
            return repo.update(existing).replaceWith(existing);
        });
    }
    
    @Path("older-than/{year}")
    @DELETE
    public Uni<Integer> cleanup(
            @PathParam("year")
            final int year
    ) {
        return withStatelessTransaction(() -> repo.deleteOlderThan(year));
    }
    
}
