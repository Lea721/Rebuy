package com.rebuy.service;

import com.rebuy.controller.dto.CartItemRequest;
import com.rebuy.controller.dto.CreateOrderRequest;
import com.rebuy.controller.dto.OrderResponse;
import com.rebuy.entity.*;
import com.rebuy.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldCreateOrder_WithValidRequest() {
        // Arrange
        User user = new User();

        Product product = new Product();
        product.setTitle("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStatus(ProductStatus.AVAILABLE);

        CartItemRequest cartItem = new CartItemRequest();
        cartItem.setProductId(1L);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setItems(List.of(cartItem));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return order;
        });

        // Act
        OrderResponse result = orderService.createOrder(request);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("99.99"), result.getTotalAmount());
        assertEquals(1, result.getItems().size());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(request));

        assertEquals("User not found", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductSold() {
        // Arrange
        User user = new User();
        Product product = new Product();
        product.setStatus(ProductStatus.SOLD);

        CartItemRequest cartItem = new CartItemRequest();
        cartItem.setProductId(1L);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setItems(List.of(cartItem));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(request));

        assertTrue(exception.getMessage().contains("Product already sold"));
        verify(orderRepository, never()).save(any());
    }
}