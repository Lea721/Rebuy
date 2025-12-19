package com.rebuy.controller;

import com.rebuy.entity.CartItem;
import com.rebuy.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getUserCart(userId));
    }

    @PostMapping("/{userId}/{productId}")
    public ResponseEntity<CartItem> addToCart(@PathVariable Long userId,
                                              @PathVariable Long productId) {
        com.rebuy.entity.CartItem item = cartService.addToCart(userId, productId);
        if (item == null) return ResponseEntity.badRequest().build();

        // If quantity == 1 -> new item created -> 201 Created
        if (item.getQuantity() == 1) {
            return ResponseEntity.status(201).body(item);
        }

        // Existing item incremented -> 200 OK
        return ResponseEntity.ok(item);
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable Long cartItemId,
                                                   @RequestBody com.rebuy.controller.dto.CartQuantityRequest req) {
        try {
            CartItem updated = cartService.updateQuantity(cartItemId, req.getQuantity());
            return ResponseEntity.ok(updated);
        } catch (java.util.NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
