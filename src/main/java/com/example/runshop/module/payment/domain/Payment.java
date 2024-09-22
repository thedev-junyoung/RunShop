package com.example.runshop.module.payment.domain;

import com.example.runshop.model.vo.payment.PaymentAmount;
import com.example.runshop.module.order.domain.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "payment")
@NoArgsConstructor
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
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "amount"))
    })
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
    // 생성자 추가
    public Payment(Order order, PaymentAmount amount) {
        this.order = order;
        this.amount = amount;
        this.status = PaymentStatus.PENDING; // 초기 상태는 PENDING
    }

    // 결제 성공 시 호출되는 메서드
    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
    }

    // 결제 실패 시 호출되는 메서드
    public void markFailure() {
        this.status = PaymentStatus.FAILURE;
    }
}
