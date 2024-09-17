package com.example.runshop.model.dto.cart;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long userId;
    private Long productId;
    private int quantity;
}
