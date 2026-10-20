package com.shopsphere.model;

import java.math.BigDecimal;

public class Product {
    private final long id;
    private final Long categoryId;
    private final String categoryName;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int stockQuantity;
    private final String imageUrl;
    private final boolean active;

    public Product(long id, Long categoryId, String categoryName, String name,
                   String description, BigDecimal price, int stockQuantity,
                   String imageUrl, boolean active) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isActive() {
        return active;
    }
}