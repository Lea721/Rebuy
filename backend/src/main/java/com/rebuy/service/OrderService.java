package com.rebuy.service;

import com.rebuy.controller.dto.OrderDetailsResponse;
import com.rebuy.entity.CartItem;
import com.rebuy.entity.Order;
import com.rebuy.entity.OrderItem;
import com.rebuy.entity.OrderStatus;
import com.rebuy.repository.CartItemRepository;
import com.rebuy.repository.OrderItemRepository;
import com.rebuy.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public OrderDetailsResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        return new OrderDetailsResponse(order, items);
    }

    public OrderDetailsResponse checkout(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal total = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PAID); // simple: assume payment success

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrderId(savedOrder.getId());
            item.setProductId(cartItem.getProductId());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(cartItem.getUnitPrice());
            item.setTotalPrice(cartItem.getTotalPrice());
            orderItemRepository.save(item);
        }

        // clear cart after checkout
        cartItemRepository.deleteAll(cartItems);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(savedOrder.getId());
        return new OrderDetailsResponse(savedOrder, orderItems);
    }
}
