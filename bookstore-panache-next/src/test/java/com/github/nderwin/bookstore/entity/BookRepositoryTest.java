package com.github.nderwin.bookstore.entity;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.quarkus.hibernate.reactive.panache.common.runtime.SessionOperations.withStatelessTransaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
public class BookRepositoryTest {
    
    @Inject
    Book.Repo repo;
    
    @RunOnVertxContext
    @Test
    public void shouldInsertAndFindBook(UniAsserter asserter) {
        Book b = new Book("Kindred", null);
        b.setYear(1979);
        
        asserter.assertThat(
                () -> withStatelessTransaction(() -> repo.insert(b)),
                v -> {}
        );
        
        asserter.assertThat(
                () -> withStatelessTransaction(() -> repo.findByTitle("Kindred")), 
                list -> {
                    assertFalse(list.isEmpty());
                    assertEquals("Kindred", list.getFirst().getTitle());
                    assertEquals(1979, list.getFirst().getYear());
                }
        );
    }
    
}
