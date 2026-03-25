package com.github.nderwin.bookstore.control;

import com.github.nderwin.bookstore.entity.Author;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

import static io.quarkus.hibernate.reactive.panache.common.runtime.SessionOperations.withStatelessTransaction;

@ApplicationScoped
public class AuthorMixedModeService {
    
    @Inject
    Author.Repo repo;
    
    @Inject
    Author.ReadRepo readRepo;
    
    // Blocking managed
    @Transactional
    public Author createAuthor(final String name, final String country) {
        final Author a = new Author(name);
        a.setCountry(country);
        
        // why not just a.persist() here?
        repo.persist(a);
        return a;
    }
    
    @Transactional
    public void renameAuthor(final long id, final String newName) {
        final Author a = repo.findById(id);
        
        if (null == a) {
            throw new NotFoundException();
        }
        
        a.setName(newName);
    }
    
    // Blocking stateless
    public void bulkImport(final List<Author> authors) {
        authors.forEach(a -> a.statelessBlocking().insert());
    }
    
    public void bulkUpdateCountry(final List<Author> authors, final String country) {
        authors.forEach(a -> {
            a.setCountry(country);
            a.statelessBlocking().update();
        });
    }
    
    // Reactive managed
    @WithTransaction
    public Uni<Author> createAuthorReactive(final String name, final String country) {
        final Author a = new Author(name);
        a.setCountry(country);
        
        return a.managedReactive().persist().replaceWith(a);
    }
    
    @WithTransaction
    public Uni<Void> renameAuthorReactive(final long id, final String newName) {
        final Author a = repo.findById(id);
        
        if (null == a) {
            return Uni.createFrom().failure(new NotFoundException());
        }
        
        a.setName(newName);
        return Uni.createFrom().voidItem();
    }
    
    // Reactive stateless
    public Uni<List<Author>> getCatalogByCountry(final String country) {
        return withStatelessTransaction(() -> readRepo.catalog(country));
    }
    
    public Uni<Void> bulkImportReactive(final List<Author> authors) {
        return withStatelessTransaction(() ->
            Uni.join()
                    .all(authors.stream()
                            .map(a -> a.statelessReactive().insert())
                            .toList())
                    .andFailFast()
                    .replaceWithVoid()
        );
    }
    
}
