package shoppingmall.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shoppingmall.model.enums.Category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "brand")
    private String brand;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    // 재고 관계 추가

    // - **Product** 1 : N **CartItem**
    // (한 상품이 여러 장바구니에 포함될 수 있음)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    // - **Product** 1 : N **OrderItem**
    // (한 상품이 여러 주문에 포함될 수 있음)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    // - **Product** 1 : 1 **Inventory**
    // (하나의 상품에 대해 하나의 재고가 관리됨)
    @OneToOne(mappedBy = "product")
    private Inventory inventory;
}
