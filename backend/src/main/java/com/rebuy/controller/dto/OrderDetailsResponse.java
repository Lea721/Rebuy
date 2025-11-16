package com.rebuy.controller.dto;

import com.rebuy.entity.Order;
import com.rebuy.entity.OrderItem;

import java.util.List;

public class OrderDetailsResponse {

    private Order order;
    private List<OrderItem> items;

    public OrderDetailsResponse() {}

    public OrderDetailsResponse(Order order, List<OrderItem> items) {
        this.order = order;
        this.items = items;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
