package com.github.nderwin.bookstore.entity;

import io.quarkus.hibernate.panache.PanacheRepository;
import io.quarkus.hibernate.panache.WithId;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.processing.Find;
import org.hibernate.annotations.processing.HQL;

import static jakarta.persistence.FetchType.EAGER;

@Table(name = "book")
@Entity
public class Book extends WithId.AutoLong {
    
    @Column(nullable = false)
    private String title;
    
    private int year;
    
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "authorid")
    private Author author;

    protected Book() {
    }

    public Book(final String title, final Author author) {
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public Author getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(final int year) {
        this.year = year;
    }

    public interface Repo extends PanacheRepository.Reactive.Stateless<Book, Long> {
        
        @Find
        Uni<List<Book>> findByTitle(String title);
        
        @HQL("WHERE year >= :year ORDER BY year DESC")
        Uni<List<Book>> findPublishedSince(int year);
        
        @HQL("DELETE FROM Book WHERE year < :year")
        Uni<Integer> deleteOlderThan(int year);
        
    }
    
}
