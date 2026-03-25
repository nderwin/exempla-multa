package com.github.nderwin.bookstore.entity;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class AuthorRepositoryTest {

    @Inject
    Author.Repo repo;
    
    @TestTransaction
    @Test
    public void shouldPersistAndFindByName() {
        final String name = "Octavia Butler";
        
        Author a = new Author(name);
        a.setCountry("US");
        repo.persist(a);
        
        Optional<Author> found = repo.findByName(name);
        assertTrue(found.isPresent());
        assertEquals("US", found.get().getCountry());
    }
    
    @TestTransaction
    @Test
    public void shouldCountByCountry() {
        for (int i = 0; i < 3; i++) {
            Author a = new Author("Author " + i);
            a.setCountry("UK");
            repo.persist(a);
        }
        
        assertEquals(3, repo.findByCountry("UK").size());
    }
    
}
