package com.rebuy.controller.dto;

import java.math.BigDecimal;

// DTO = what Angular sends to backend when creating/updating product
public class ProductRequest {

    private String title;
    private String description;
    private BigDecimal price;

    private String category;
    private String condition;
    private String location;

    // URL returned by /api/upload/image
    private String imageUrl;

    // ID of the user publishing the product
    private Long sellerId;

    public ProductRequest() {}

    // ===== Getters & Setters =====

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
}
