package com.github.nderwin.bookstore.entity;

import io.quarkus.hibernate.panache.PanacheEntity;
import io.quarkus.hibernate.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Optional;
import org.hibernate.annotations.processing.Find;
import org.hibernate.annotations.processing.HQL;

@Table(name = "author")
@Entity
public class Author extends PanacheEntity {
    
    @Column(nullable = false)
    private String name;
    
    private String country;

    protected Author() {
    }

    public Author(final String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public interface Repo extends PanacheRepository<Author> {
        
        @Find
        Optional<Author> findByName(String name);
        
        @HQL("WHERE country = :country ORDER BY name")
        List<Author> findByCountry(String country);
        
        @HQL("DELETE FROM Author WHERE country = :country")
        long deleteByCountry(String country);
        
    }
    
    public interface ReadRepo extends PanacheRepository.Reactive.Stateless<Author, Long> {
        
        @HQL("WHERE country = :country ORDER BY name")
        Uni<List<Author>> catalog(String country);
        
    }
    
}
