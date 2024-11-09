package com.example.runshop.module.order.domain;

import com.example.runshop.exception.order.OrderAlreadyBeenCancelledException;
import com.example.runshop.module.payment.domain.Payment;
import com.example.runshop.model.entity.User;
import jakarta.persistence.*;
import com.example.runshop.model.enums.OrderStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders") // 테이블 이름을 'orders' 로 변경
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    OrderStatus status;
    @Column(name = "order_date", nullable = false)
    LocalDateTime orderDate;
    @Column(name = "payment_date")
    LocalDateTime paymentDate;

    @Column(name = "total_price", nullable = false)
    BigDecimal totalPrice;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // - **Order** 1 : N **OrderItem**
    // (한 주문에 여러 상품이 포함될 수 있음)
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    // - **Order** 1 : 1 **Payment**
    // (한 주문에 대해 하나의 결제가 이루어짐)
    @OneToOne(mappedBy = "order")
    private Payment payment;

// Business Logic
// ==== 메서드 ====================================
    protected Order() {}
    public Order(User user, BigDecimal totalPrice, List<OrderItem> items) {
        this.totalPrice = totalPrice;
        this.user = user;
        this.status = OrderStatus.PENDING;
        this.orderItems.addAll(items);
    }
    public static Order create(User user, BigDecimal totalPrice, List<OrderItem> orderItems) {
        orderItems.forEach(OrderItem::decreaseStock);
        return new Order(user, totalPrice, orderItems);
    }

    public void completePayment() {
        this.status = OrderStatus.PAYMENT_COMPLETE;
    }

    public void cancelOrder() {
        if (this.status == OrderStatus.ORDER_CANCELLATION) {
            throw new OrderAlreadyBeenCancelledException("이미 취소된 주문입니다.");
        }
        this.status = OrderStatus.ORDER_CANCELLATION;
        orderItems.forEach(OrderItem::restoreStock);
    }


}