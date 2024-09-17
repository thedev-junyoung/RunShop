package com.example.runshop.model.dto;

import com.example.runshop.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class OrderItemDTO {
    private Long id;
    private Product product;
    private int quantity;
    private double price;
}
