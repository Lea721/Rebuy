package com.rebuy.service;

import com.rebuy.entity.CartItem;
import com.rebuy.entity.Product;
import com.rebuy.entity.ProductStatus;
import com.rebuy.entity.User;
import com.rebuy.repository.CartItemRepository;
import com.rebuy.repository.ProductRepository;
import com.rebuy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {

        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // GET: all cart items for a user
    public List<CartItem> getUserCart(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    // POST: add product to cart
    public CartItem addToCart(Long userId, Long productId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Cannot add sold products
        if (product.getStatus() == ProductStatus.SOLD) {
            throw new IllegalArgumentException("Product already sold");
        }

        // Prevent duplicate items
        if (cartItemRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            return null; // already in cart
        }

        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);

        return cartItemRepository.save(item);
    }

    // DELETE: remove single item
    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // DELETE: clear whole cart
    public void clearCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(items);
    }
}
