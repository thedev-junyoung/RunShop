package com.example.runshop.module.payment.domain;

import com.example.runshop.model.vo.payment.PaymentAmount;
import com.example.runshop.module.order.domain.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Getter
@Table(name = "payment")
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private PaymentMethod method;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(STRING)
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
    public Payment(Order order, PaymentAmount amount, PaymentMethod method) {
        this.order = order;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    // 정적 팩토리 메서드
    public static Payment create(Order order, PaymentAmount amount, PaymentMethod method) {
        return new Payment(order, amount, method);
    }

    public void process(boolean success) {
        if (success) {
            this.status = PaymentStatus.SUCCESS;
            order.completePayment();
        } else {
            this.status = PaymentStatus.FAILURE;
//            order.cancelOrder();
        }
    }
}
