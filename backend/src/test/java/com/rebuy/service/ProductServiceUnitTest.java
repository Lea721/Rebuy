package com.rebuy.service;

import com.rebuy.controller.dto.ProductRequest;
import com.rebuy.entity.Product;
import com.rebuy.entity.ProductStatus;
import com.rebuy.entity.User;
import com.rebuy.repository.ProductRepository;
import com.rebuy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAll_ShouldReturnAllProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // Act
        List<Product> result = productService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getTitle());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.getById(999L));

        assertEquals("Product not found", exception.getMessage());
    }

    // In ProductServiceUnitTest.java - update the create test
    @Test
    void create_ShouldCreateProduct_WithValidRequest() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setTitle("New Product");
        request.setDescription("Product Description");
        request.setPrice(new BigDecimal("99.99"));
        request.setCategory("Electronics");
        request.setSellerId(1L);

        User seller = new User();

        Product savedProduct = new Product();
        savedProduct.setTitle("New Product");
        savedProduct.setStatus(ProductStatus.AVAILABLE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product result = productService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("New Product", result.getTitle());
        assertEquals(ProductStatus.AVAILABLE, result.getStatus());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getAvailable_ShouldReturnOnlyAvailableProducts() {
        // Arrange
        Product availableProduct = new Product();
        availableProduct.setStatus(ProductStatus.AVAILABLE);

        when(productRepository.findByStatus(ProductStatus.AVAILABLE))
                .thenReturn(List.of(availableProduct));

        // Act
        List<Product> result = productService.getAvailable();

        // Assert
        assertEquals(1, result.size());
        assertEquals(ProductStatus.AVAILABLE, result.get(0).getStatus());
        verify(productRepository, times(1)).findByStatus(ProductStatus.AVAILABLE);
    }
}