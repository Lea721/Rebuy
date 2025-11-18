package com.rebuy.controller.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long productId;
    private String productTitle;
    private BigDecimal price;

    public OrderItemResponse() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
