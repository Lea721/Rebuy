package com.rebuy.controller;

import com.rebuy.controller.dto.CreateOrderRequest;
import com.rebuy.controller.dto.OrderResponse;
import com.rebuy.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse resp = orderService.createOrder(request);
        return ResponseEntity.created(java.net.URI.create("/api/orders/" + resp.getOrderId())).body(resp);
    }
}
