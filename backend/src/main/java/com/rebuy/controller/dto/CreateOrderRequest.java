package com.rebuy.controller.dto;

import java.util.List;

public class CreateOrderRequest {

    private Long userId;
    private List<CartItemRequest> items;

    public CreateOrderRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<CartItemRequest> getItems() { return items; }
    public void setItems(List<CartItemRequest> items) { this.items = items; }
}
