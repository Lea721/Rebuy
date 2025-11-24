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

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.service-key:}")
    private String supabaseServiceKey;

    @Value("${supabase.bucket:public}")
    private String bucket;

    private final RestTemplate rest = new RestTemplate();

    public String uploadProductImage(Long productId, MultipartFile file) throws IOException {
        if (supabaseUrl == null || supabaseUrl.isBlank() || supabaseServiceKey == null || supabaseServiceKey.isBlank()) {
            throw new IllegalStateException("Supabase storage is not configured (supabase.url or supabase.service-key is missing)");
        }

        String filename = Instant.now().toEpochMilli() + "_" + file.getOriginalFilename();
        String objectPath = "products/" + productId + "/" + filename;

        String endpoint = supabaseUrl;
        if (endpoint.endsWith("/")) endpoint = endpoint.substring(0, endpoint.length() - 1);
        // Storage upload endpoint (PUT to object path)
        String uploadUrl = endpoint + "/storage/v1/object/" + bucket + "/" + objectPath;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseServiceKey);
        headers.setContentType(MediaType.parseMediaType(file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE));
        // allow overwriting if exists
        headers.add("x-upsert", "true");

        byte[] body = StreamUtils.copyToByteArray(file.getInputStream());

        HttpEntity<byte[]> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = rest.exchange(URI.create(uploadUrl), HttpMethod.PUT, request, String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to upload to Supabase Storage: " + resp.getStatusCode() + " - " + resp.getBody());
        }

        // Public URL for stored object (public bucket)
        String publicUrl = endpoint + "/storage/v1/object/public/" + bucket + "/" + objectPath;
        return publicUrl;
    }
}
