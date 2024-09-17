package com.example.runshop.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 재고 수량
    @Column(nullable = false)
    private int stockQuantity;

    // - **Product** 1 : 1 **Inventory**
    // (하나의 상품에 대해 하나의 재고가 관리됨)

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 재고 증가 메서드
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // 재고 감소 메서드
    public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stockQuantity -= quantity;
    }
}
