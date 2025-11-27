package com.rebuy.repository;

import com.rebuy.entity.Product;
import com.rebuy.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // =========================
    //  Custom Finder Methods
    // =========================

    // Find all products with a specific status
    // Example: AVAILABLE or SOLD
    List<Product> findByStatus(ProductStatus status);

    // Find products by category (case-insensitive)
    // Example: "Electronics", "Clothing"
    List<Product> findByCategoryIgnoreCase(String category);

    // =========================
    //  OPTIONAL USEFUL QUERIES (you can add later)
    // =========================

    // Find all products published by a specific user (seller)
    // This requires the ManyToOne relation in Product
    List<Product> findBySellerId(Long sellerId);

    // Find products by location (ex: Beirut)
    List<Product> findByLocationIgnoreCase(String location);

    // Search products by title containing a word
    // Example: search "iphone"
    List<Product> findByTitleContainingIgnoreCase(String keyword);
}
