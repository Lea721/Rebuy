package com.rebuy.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;

    public OrderResponse() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
}
