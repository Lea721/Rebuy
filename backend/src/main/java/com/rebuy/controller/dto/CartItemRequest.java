package com.rebuy.controller.dto;

public class CartItemRequest {

    private Long productId;

    public CartItemRequest() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
}
