package shoppingmall.model.entity;

import jakarta.persistence.*;
import shoppingmall.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 테이블 이름을 'orders'로 변경
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    OrderStatus status;
    @Column(name = "order_date", nullable = false)
    LocalDateTime orderDate;
    @Column(name = "total_price", nullable = false)
    double totalPrice;

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
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Payment payment;
}