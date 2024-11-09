package com.example.runshop.model.entity;

import com.example.runshop.exception.cartitem.InvalidCartItemException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "cart_item")
@Builder
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;

    //- **CartItem** N : 1 **Product**
    // (한 상품이 여러 장바구니에 포함될 수 있음)
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // - **CartItem** N : 1 **User**
    // (여러 상품이 한 사용자의 장바구니에 포함될 수 있음)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    // 장바구니 아이템 생성
    public static CartItem createCartItem(User user, Product product, int quantity) {
        validateQuantity(quantity);
        return CartItem.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .build();
    }
    // 장바구니 수량 증가
    public void increaseQuantity(int additionalQuantity) {
        validateQuantity(additionalQuantity);
        this.quantity += additionalQuantity;
    }

    // 수량 검증 메서드
    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidCartItemException("장바구니 항목의 수량은 0보다 커야 합니다.");
        }
    }
}
