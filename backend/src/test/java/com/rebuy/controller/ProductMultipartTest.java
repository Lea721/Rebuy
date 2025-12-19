package com.rebuy.controller;

import com.rebuy.repository.ProductRepository;
import com.rebuy.service.SupabaseStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductMultipartTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private com.rebuy.repository.UserRepository userRepository;

    @MockBean
    private SupabaseStorageService supabaseStorageService;

    @BeforeEach
    void beforeEach() {
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createProductWithImageMultipart() throws Exception {
        when(supabaseStorageService.uploadProductImage(anyLong(), any())).thenReturn("https://supabase/storage/public/products/1/photo.jpg");

        // create a seller user and use its id
        com.rebuy.entity.User seller = new com.rebuy.entity.User();
        seller.setEmail("seller@example.com");
        seller.setPassword("secret");
        seller.setName("Seller");
        seller = userRepository.save(seller);

        String productJson = String.format("{\"title\":\"Prod\",\"description\":\"d\",\"price\":9.99,\"category\":\"X\",\"condition\":\"New\",\"location\":\"Beirut\",\"sellerId\":%d}", seller.getId());

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy-image".getBytes());
        MockMultipartFile productPart = new MockMultipartFile("product", "product.json", MediaType.APPLICATION_JSON_VALUE, productJson.getBytes());

        mockMvc.perform(multipart("/api/products/with-image").file(file).file(productPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("https://supabase/storage/public/products/1/photo.jpg"));
    }
}
