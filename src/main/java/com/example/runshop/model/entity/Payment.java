package com.example.runshop.model.entity;

import com.example.runshop.model.vo.payment.PaymentAmount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.example.runshop.model.enums.PaymentMethod;
import com.example.runshop.model.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;


    // 결제 금액을 VO로 변경
    @Embedded
    private PaymentAmount amount;


    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }

    // - **Payment** 1 : 1 **Order**
    // (한 결제가 하나의 주문에 대해 이루어짐)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

}
