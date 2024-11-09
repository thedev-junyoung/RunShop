package com.example.runshop.module.order.domain;

import com.example.runshop.model.entity.Product;
import com.example.runshop.model.vo.orderitem.OrderQuantity;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.module.payment.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "order_item")
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "quantity"))
    })
    private OrderQuantity quantity;

    @Transient
    @Embedded
    private ProductName productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private OrderItem(Product product, OrderQuantity quantity, PaymentMethod paymentMethod) {
        this.product = product;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
        this.productName = product.getName();  // `ProductName` 객체로 설정
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public static OrderItem create(Product product, OrderQuantity quantity, PaymentMethod paymentMethod) {
        return new OrderItem(product, quantity, paymentMethod);
    }

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }

    // 주문 시 재고 감소 메서드
    public void decreaseStock() {
        product.getInventory().decreaseStock(quantity.value());
    }

    // 주문 취소 시 재고 복구 메서드
    public void restoreStock() {
        product.getInventory().increaseStock(quantity.value());
    }

    // 결제 성공 시 상태 변경 메서드
    public void completePayment() {
        this.paymentStatus = PaymentStatus.SUCCESS;
    }

    // 결제 실패 시 상태 변경 메서드
    public void cancelPayment() {
        this.paymentStatus = PaymentStatus.FAILURE;
    }

    // Order 엔티티 설정 메서드
    public void assignToOrder(Order order) {
        this.order = order;
    }
}
