package com.example.runshop.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.example.runshop.model.enums.PaymentMethod;
import com.example.runshop.model.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    // 주문 상품의 수량
    @Column(name = "quantity", nullable = false)
    private int quantity;


    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }

    // - **OrderItem** N : 1 **Order**
    // (여러 상품이 하나의 주문에 포함될 수 있음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;


    // - **OrderItem** N : 1 **Product**
    // (한 상품이 여러 주문에 포함될 수 있음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
