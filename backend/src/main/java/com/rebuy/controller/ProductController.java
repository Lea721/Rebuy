package com.rebuy.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebuy.controller.dto.ProductRequest;
import com.rebuy.entity.Product;
import com.rebuy.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Allow Angular frontend to access these APIs
public class ProductController {

    private final ProductService productService;
    private final com.rebuy.service.SupabaseStorageService supabaseStorageService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService,
                             com.rebuy.service.SupabaseStorageService supabaseStorageService,
                             ObjectMapper objectMapper) {
        this.productService = productService;
        this.supabaseStorageService = supabaseStorageService;
        this.objectMapper = objectMapper;
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


    // ===================== GET PRODUCTS BY USER (SELLER) =====================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Product>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(productService.getBySellerId(userId));
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

    // Create product and upload image in a single multipart request
    @PostMapping(value = "/with-image", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> createWithImage(@RequestPart("product") String productJson,
                                                   @RequestPart("file") org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {

        com.rebuy.controller.dto.ProductRequest request = objectMapper.readValue(productJson, com.rebuy.controller.dto.ProductRequest.class);

        Product created = productService.create(request);

        // upload file to Supabase via injected service
        String publicUrl = null;
        try {
            publicUrl = supabaseStorageService.uploadProductImage(created.getId(), file);
        } catch (Exception ex) {
            log.warn("Image upload failed for product {}: {}", created.getId(), ex.getMessage());
        }

        if (publicUrl != null) {
            productService.updateImageUrl(created.getId(), publicUrl, file.getContentType(), file.getOriginalFilename());
            created.setImageUrl(publicUrl);
            created.setImageContentType(file.getContentType());
            created.setImageFilename(file.getOriginalFilename());
        }

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
