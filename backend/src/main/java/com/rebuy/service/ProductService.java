package com.rebuy.service;

import com.rebuy.controller.dto.ProductRequest;
import com.rebuy.entity.Product;
import com.rebuy.entity.ProductStatus;
import com.rebuy.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Product create(ProductRequest request) {
        Product product = new Product();
        mapRequestToProduct(request, product);
        product.setStatus(ProductStatus.AVAILABLE);
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

    private void mapRequestToProduct(ProductRequest request, Product product) {
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setCondition(request.getCondition());
        product.setLocation(request.getLocation());
        product.setImageUrl(request.getImageUrl());
        product.setSellerId(request.getSellerId());
    }
}
