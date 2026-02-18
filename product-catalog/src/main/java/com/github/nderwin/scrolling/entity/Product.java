package com.github.nderwin.scrolling.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "product", indexes = {
    @Index(name = "idx_category_views_id", columnList = "category, view_count DESC, id"),
    @Index(name = "idx_category_created_id", columnList = "category, created_at DESC, id")
})
@Entity
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(name = "view_count", nullable = false)
    private int viewCount;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "image_url")
    private String imageUrl;

    protected Product() {
    }

    public Product(
            final String name, 
            final String category, 
            final BigDecimal price,
            final int viewCount,
            final Instant createdAt
    ) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getViewCount() {
        return viewCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }
   
}
