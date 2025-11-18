package com.rebuy.service;

import com.rebuy.controller.dto.ProductRequest;
import com.rebuy.entity.Product;
import com.rebuy.entity.ProductStatus;
import com.rebuy.entity.User;
import com.rebuy.repository.ProductRepository;
import com.rebuy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    // ===================== BASIC CRUD =====================

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Product create(ProductRequest request) {

        Product product = new Product();

        // Fill fields from request
        mapRequestToProduct(request, product);

        // Default status = AVAILABLE
        product.setStatus(ProductStatus.AVAILABLE);

        // Save to database
        return productRepository.save(product);
    }


    public Product update(Long id, ProductRequest request) {
        Product product = getById(id);
        mapRequestToProduct(request, product);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getAvailable() {
        return productRepository.findByStatus(ProductStatus.AVAILABLE);
    }


    // ===================== MAPPING HELPER =====================

    private void mapRequestToProduct(ProductRequest request, Product product) {

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setCondition(request.getCondition());
        product.setLocation(request.getLocation());
        product.setImageUrl(request.getImageUrl());

        // ============ FIX: Map seller correctly ============
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        product.setSeller(seller);
    }
}
