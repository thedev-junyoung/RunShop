package com.example.shoppingmall.model.entity;

import jakarta.persistence.*;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;

    //- **CartItem** N : 1 **Product**
    // (한 상품이 여러 장바구니에 포함될 수 있음)
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // - **CartItem** N : 1 **Cart**
    // (여러 상품이 하나의 장바구니에 포함될 수 있음)
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
