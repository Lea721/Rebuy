package com.rebuy.controller;

import com.rebuy.controller.dto.ProductRequest;
import com.rebuy.entity.Product;
import com.rebuy.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Allow Angular frontend to access these APIs
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    // ===================== GET ALL PRODUCTS =====================
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }


    // ===================== GET AVAILABLE PRODUCTS =====================
    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailable() {
        return ResponseEntity.ok(productService.getAvailable());
    }


    // ===================== GET PRODUCT BY ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }


    // ===================== CREATE PRODUCT =====================
    // IMPORTANT:
    // Angular must send JSON here AFTER uploading the image
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequest request) {
        Product created = productService.create(request);
        return ResponseEntity.ok(created);
    }


    // ===================== UPDATE PRODUCT =====================
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id,
                                          @RequestBody ProductRequest request) {
        Product updated = productService.update(id, request);
        return ResponseEntity.ok(updated);
    }


    // ===================== DELETE PRODUCT =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
