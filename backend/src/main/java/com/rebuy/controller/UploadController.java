package com.rebuy.controller;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rebuy.entity.Product;
import com.rebuy.repository.ProductRepository;
import com.rebuy.service.SupabaseStorageService;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin("*")
public class UploadController {

    private final ProductRepository productRepository;
    private final SupabaseStorageService supabaseStorageService;

    public UploadController(ProductRepository productRepository, SupabaseStorageService supabaseStorageService) {
        this.productRepository = productRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    // Upload image and save it in the database associated with a product
    @PostMapping("/product/{productId}/image")
    public ResponseEntity<?> uploadProductImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) throws IOException {
        Optional<Product> maybe = productRepository.findById(productId);
        if (maybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = maybe.get();

        // upload to Supabase Storage and store public URL on product
        String publicUrl = supabaseStorageService.uploadProductImage(productId, file);

        product.setImageUrl(publicUrl);
        product.setImageContentType(file.getContentType());
        product.setImageFilename(file.getOriginalFilename());

        productRepository.save(product);

        return ResponseEntity.created(URI.create(publicUrl)).body(publicUrl);
    }

    // Serve product image bytes from DB
    @GetMapping("/product/{productId}/image")
    public ResponseEntity<?> getProductImage(@PathVariable Long productId) {
        Optional<Product> maybe = productRepository.findById(productId);
        if (maybe.isEmpty()) return ResponseEntity.notFound().build();

        Product product = maybe.get();
        // If product has an external URL (e.g., Supabase storage), redirect to it so clients fetch directly
        if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
            return ResponseEntity.status(302).location(URI.create(product.getImageUrl())).build();
        }

        // No image stored
        return ResponseEntity.noContent().build();
    }
}
