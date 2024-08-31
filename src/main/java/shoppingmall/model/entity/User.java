package shoppingmall.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import shoppingmall.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name= "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // mappedBy: 연관관계 주인이 아님을 나타냄 -> 외래키가 있는 곳이 주인
    // 즉 cart, order 테이블에 있는 user_id가 외래키이므로 user 테이블이 연관관계 주인이 아님

    // 관계 설정해야함
    // 유저는 여러개의 주문을 가질 수 있다.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    // 유저는 하나의 장바구니를 가질 수 있다.
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
}
