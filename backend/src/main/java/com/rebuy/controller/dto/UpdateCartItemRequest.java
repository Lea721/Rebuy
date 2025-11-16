package com.rebuy.controller.dto;

public class UpdateCartItemRequest {

    private int quantity;

    public UpdateCartItemRequest() {}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
