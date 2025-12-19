package com.rebuy.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "products") // This will create/update the "products" table in Supabase
public class Product {

    // ==================== PRIMARY KEY ====================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique ID for each product


    // ==================== BASIC PRODUCT INFO ====================

    @Column(nullable = false, length = 150)
    private String title; // Product title (example: "iPhone 12")

    @Column(nullable = false, length = 1000)
    private String description; // Full product description

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Price with decimals (ex: 299.99)



    // ==================== CATEGORY / CONDITION / LOCATION ====================

    @Column(length = 100)
    private String category; // Example: "Electronics", "Clothing"

    @Column(length = 50)
    private String condition; // Example: "New", "Used - Excellent"

    @Column(length = 100)
    private String location; // Example: "Beirut", "Tripoli"



    // ==================== IMAGE ====================

    @Column(length = 500)
    private String imageUrl; // Temporary: only 1 image. Later we can add multiple images.

    @Column(name = "image_content_type", length = 100)
    private String imageContentType;

    @Column(name = "image_filename", length = 255)
    private String imageFilename;


    // ==================== STATUS ====================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.AVAILABLE;
    // AVAILABLE or SOLD



    // ==================== SELLER RELATION (IMPORTANT) ====================

    // Many products → One seller
    // This allows: product.getSeller(), seller.getProducts(), automatic joins in JPA
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller; // The user who published this product



    // ==================== TIMESTAMPS ====================

    private LocalDateTime createdAt; // Date when product was created
    private LocalDateTime updatedAt; // Date when product was last updated

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now(); // Automatically set on insert
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now(); // Automatically set on update
    }



    // ==================== CONSTRUCTOR ====================

    public Product() {}



    // ==================== GETTERS & SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getImageContentType() { return imageContentType; }
    public void setImageContentType(String imageContentType) { this.imageContentType = imageContentType; }

    public String getImageFilename() { return imageFilename; }
    public void setImageFilename(String imageFilename) { this.imageFilename = imageFilename; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
