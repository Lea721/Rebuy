package com.rebuy.service;

import com.rebuy.controller.dto.AddToCartRequest;
import com.rebuy.controller.dto.UpdateCartItemRequest;
import com.rebuy.entity.CartItem;
import com.rebuy.entity.Product;
import com.rebuy.repository.CartItemRepository;
import com.rebuy.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartForUser(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem addToCart(AddToCartRequest request) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Check if item already in cart
        CartItem item = cartItemRepository
                .findByUserIdAndProductId(request.getUserId(), request.getProductId())
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setUserId(request.getUserId());
            item.setProductId(request.getProductId());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(request.getQuantity());
        } else {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        }

        return cartItemRepository.save(item);
    }

    public CartItem updateCartItem(Long itemId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (request.getQuantity() <= 0) {
            // quantity <= 0 → remove item
            cartItemRepository.delete(item);
            return null;
        }

        item.setQuantity(request.getQuantity());
        return cartItemRepository.save(item);
    }

    public void removeItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    public void clearCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(items);
    }
}
