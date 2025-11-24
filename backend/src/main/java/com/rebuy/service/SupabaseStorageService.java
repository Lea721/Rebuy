package com.rebuy.service;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String supabaseServiceKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadProductImage(Long productId, MultipartFile file) throws IOException {

        if (supabaseUrl == null || supabaseUrl.isBlank()) {
            throw new IllegalStateException("Supabase URL missing in application.properties");
        }

        if (supabaseServiceKey == null || supabaseServiceKey.isBlank()) {
            throw new IllegalStateException("Supabase service key missing in application.properties");
        }

        // Trim ANY hidden spaces or trailing slash
        String endpoint = supabaseUrl.trim();
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

        // file name
        String filename = Instant.now().toEpochMilli() + "_" + file.getOriginalFilename();

        // Create path inside bucket
        String objectPath = "products/" + productId + "/" + filename;

        // Supabase upload endpoint
        String uploadUrl = endpoint + "/storage/v1/object/" + bucket + "/" + objectPath;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseServiceKey);

        // Safe content-type fallback
        String type = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        headers.setContentType(MediaType.parseMediaType(type));

        // allow creating or overwriting
        headers.add("x-upsert", "true");

        byte[] fileBytes = StreamUtils.copyToByteArray(file.getInputStream());

        HttpEntity<byte[]> request = new HttpEntity<>(fileBytes, headers);

        // Upload file
        ResponseEntity<String> resp = restTemplate.exchange(
                URI.create(uploadUrl),
                HttpMethod.PUT,
                request,
                String.class
        );

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to upload to Supabase: " + resp.getStatusCode());
        }

        // Public URL
        return endpoint + "/storage/v1/object/public/" + bucket + "/" + objectPath;
    }
}
