package com.rebuy.controller;

import com.rebuy.controller.dto.OrderDetailsResponse;
import com.rebuy.entity.Order;
import com.rebuy.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<OrderDetailsResponse> checkout(@PathVariable Long userId) {
        OrderDetailsResponse response = orderService.checkout(userId);
        return ResponseEntity.ok(response);
    }
}
