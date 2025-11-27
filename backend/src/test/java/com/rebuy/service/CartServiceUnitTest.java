package com.rebuy.service;

import com.rebuy.entity.CartItem;
import com.rebuy.entity.Product;
import com.rebuy.entity.ProductStatus;
import com.rebuy.entity.User;
import com.rebuy.repository.CartItemRepository;
import com.rebuy.repository.ProductRepository;
import com.rebuy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceUnitTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCart_ShouldAddNewItem_WhenNotInCart() {
        // Arrange
        User user = new User();
        Product product = new Product();
        product.setStatus(ProductStatus.AVAILABLE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem item = invocation.getArgument(0);
            // Don't set ID - your entity doesn't have setId method
            return item;
        });

        // Act
        CartItem result = cartService.addToCart(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getQuantity());
        assertEquals(user, result.getUser());
        assertEquals(product, result.getProduct());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addToCart_ShouldIncrementQuantity_WhenAlreadyInCart() {
        // Arrange
        User user = new User();
        Product product = new Product();
        product.setStatus(ProductStatus.AVAILABLE);

        CartItem existingItem = new CartItem();
        existingItem.setQuantity(1); // Use the setter that exists

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(existingItem)).thenReturn(existingItem);

        // Act
        CartItem result = cartService.addToCart(1L, 1L);

        // Assert
        assertEquals(2, result.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    void addToCart_ShouldThrowException_WhenProductSold() {
        // Arrange
        User user = new User();
        Product product = new Product();
        product.setStatus(ProductStatus.SOLD);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart(1L, 1L));

        assertEquals("Product already sold", exception.getMessage());
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void getUserCart_ShouldReturnUserCartItems() {
        // Arrange
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(2);

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        // Act
        List<CartItem> result = cartService.getUserCart(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getQuantity());
        verify(cartItemRepository, times(1)).findByUserId(1L);
    }

    @Test
    void updateQuantity_ShouldUpdateQuantity_WhenValid() {
        // Arrange
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        // Act
        CartItem result = cartService.updateQuantity(1L, 5);

        // Assert
        assertEquals(5, result.getQuantity());
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    void updateQuantity_ShouldThrowException_WhenQuantityLessThan1() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartService.updateQuantity(1L, 0));

        assertEquals("Quantity must be at least 1", exception.getMessage());
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void removeItem_ShouldDeleteCartItem() {
        // Act
        cartService.removeItem(1L);

        // Assert
        verify(cartItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void clearCart_ShouldDeleteAllUserItems() {
        // Arrange
        CartItem item1 = new CartItem();
        CartItem item2 = new CartItem();

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(item1, item2));

        // Act
        cartService.clearCart(1L);

        // Assert
        verify(cartItemRepository, times(1)).deleteAll(List.of(item1, item2));
    }
}