package shoppingmall.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "create_at")
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    // 1:1 관계 - 유저는 하나의 장바구니를 가진다.
    // 외래키가 있는 곳이 주인, 즉 cart 테이블이 유저-카드 테이블에서 연관관계 주인
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // - **Cart** 1 : N **CartItem**
    // (한 장바구니에 여러 상품이 담길 수 있음)
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();


}
