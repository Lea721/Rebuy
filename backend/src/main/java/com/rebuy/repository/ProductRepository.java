package com.rebuy.repository;

import com.rebuy.entity.Product;
import com.rebuy.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryIgnoreCase(String category);
}
