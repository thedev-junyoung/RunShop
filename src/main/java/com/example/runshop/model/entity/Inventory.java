package com.example.runshop.model.entity;

import com.example.runshop.model.vo.inventory.StockQuantity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 재고 수량

    @Embedded
    private StockQuantity stockQuantity;

    // - **Product** 1 : 1 **Inventory**
    // (하나의 상품에 대해 하나의 재고가 관리됨)

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;


}
