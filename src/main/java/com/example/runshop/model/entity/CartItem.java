package com.example.runshop.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;

    //- **CartItem** N : 1 **Product**
    // (한 상품이 여러 장바구니에 포함될 수 있음)
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // - **CartItem** N : 1 **User**
    // (여러 상품이 한 사용자의 장바구니에 포함될 수 있음)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
