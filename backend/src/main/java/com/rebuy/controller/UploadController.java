package com.rebuy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin("*")
public class UploadController implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        // Create uploads/ folder if not exists
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // unique file name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // save file
        Path filepath = Paths.get(UPLOAD_DIR, fileName);
        Files.write(filepath, file.getBytes());

        // return URL to Angular
        String imageUrl = "http://localhost:8080/uploads/" + fileName;

        return ResponseEntity.ok(imageUrl);
    }

    // allow Spring Boot to serve images publicly
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOAD_DIR);
    }
}
