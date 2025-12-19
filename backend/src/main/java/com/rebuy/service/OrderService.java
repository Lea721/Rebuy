package com.rebuy.service;

import com.rebuy.controller.dto.*;
import com.rebuy.entity.*;
import com.rebuy.repository.OrderItemRepository;
import com.rebuy.repository.OrderRepository;
import com.rebuy.repository.ProductRepository;
import com.rebuy.repository.CartItemRepository;
import com.rebuy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItemRequest cartItem : request.getItems()) {

            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (product.getStatus() == ProductStatus.SOLD) {
                throw new IllegalArgumentException("Product already sold: " + product.getTitle());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
            item.setTotalPrice(product.getPrice());

            // mark product as SOLD
            product.setStatus(ProductStatus.SOLD);
            productRepository.save(product);

            items.add(item);

            total = total.add(product.getPrice());
        }

        order.setTotalAmount(total);
        order.setItems(items);
        orderRepository.save(order);

        // Remove purchased items from the buyer's cart (if present)
        try {
            for (CartItemRequest cartItemReq : request.getItems()) {
                cartItemRepository.findByUserIdAndProductId(request.getUserId(), cartItemReq.getProductId())
                        .ifPresent(ci -> cartItemRepository.deleteById(ci.getId()));
            }
        } catch (Exception ex) {
            // non-fatal: log and continue
            // (we don't want checkout to fail because cart cleanup had an issue)
            org.slf4j.LoggerFactory.getLogger(OrderService.class).warn("Failed to clear cart items after order: {}", ex.getMessage());
        }

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem i : items) {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setProductId(i.getProduct().getId());
            ir.setProductTitle(i.getProduct().getTitle());
            ir.setPrice(i.getUnitPrice());
            itemResponses.add(ir);
        }

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setTotalAmount(total);
        response.setItems(itemResponses);

        return response;
    }
}
