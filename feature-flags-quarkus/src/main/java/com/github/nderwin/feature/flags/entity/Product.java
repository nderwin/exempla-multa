package com.github.nderwin.feature.flags.entity;

import java.math.BigDecimal;

public class Product {

    private Long id;
    
    private String name;
    
    private String category;
    
    private BigDecimal price;
    
    private String sku;

    protected Product() {
    }

    public Product(final Long id, final String name, final String category, final BigDecimal price, final String sku) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.sku = sku;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }
    
}
