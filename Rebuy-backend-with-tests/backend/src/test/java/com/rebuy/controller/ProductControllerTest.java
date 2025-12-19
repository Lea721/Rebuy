package com.rebuy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebuy.controller.dto.ProductRequest;
import com.rebuy.entity.Product;
import com.rebuy.service.ProductService;
import com.rebuy.service.SupabaseStorageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductController productController;

    private Product mockProduct;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setTitle("Test Product");
    }

    @Test
    void testGetAll() throws Exception {
        when(productService.getAll()).thenReturn(List.of(mockProduct));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetAvailable() throws Exception {
        when(productService.getAvailable()).thenReturn(List.of(mockProduct));

        mockMvc.perform(get("/api/products/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Product"));
    }

    @Test
    void testGetByUser() throws Exception {
        when(productService.getBySellerId(10L)).thenReturn(List.of(mockProduct));

        mockMvc.perform(get("/api/products/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetById() throws Exception {
        when(productService.getById(1L)).thenReturn(mockProduct);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Product"));
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductRequest req = new ProductRequest();
        req.setTitle("New Product");

        when(productService.create(any())).thenReturn(mockProduct);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\":\"New Product\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateWithImageSuccess() throws Exception {
        ProductRequest req = new ProductRequest();
        req.setTitle("Image Product");

        MockMultipartFile productJson = new MockMultipartFile(
                "product", "", "application/json",
                "{\"title\":\"Image Product\"}".getBytes()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", "image/jpeg", "img".getBytes()
        );

        when(objectMapper.readValue(any(byte[].class), eq(ProductRequest.class))).thenReturn(req);
        when(productService.create(any())).thenReturn(mockProduct);
        when(supabaseStorageService.uploadProductImage(eq(1L), any())).thenReturn("url");

        mockMvc.perform(
                        multipart("/api/products/with-image")
                                .file(productJson)
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateWithImageUploadFails() throws Exception {
        ProductRequest req = new ProductRequest();
        req.setTitle("Image Product");

        MockMultipartFile productJson = new MockMultipartFile(
                "product", "", "application/json",
                "{\"title\":\"Image Product\"}".getBytes()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", "image/jpeg", "img".getBytes()
        );

        when(objectMapper.readValue(any(byte[].class), eq(ProductRequest.class))).thenReturn(req);
        when(productService.create(any())).thenReturn(mockProduct);
        when(supabaseStorageService.uploadProductImage(eq(1L), any())).thenThrow(new RuntimeException("err"));

        mockMvc.perform(
                        multipart("/api/products/with-image")
                                .file(productJson)
                                .file(file)
                )
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProduct() throws Exception {
        ProductRequest req = new ProductRequest();
        req.setTitle("Updated");

        when(productService.update(eq(1L), any())).thenReturn(mockProduct);

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\":\"Updated\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
