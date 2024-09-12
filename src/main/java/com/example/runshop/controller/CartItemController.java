package com.example.runshop.controller;

import com.example.runshop.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    // 장바구니에 상품 추가
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long productId, @RequestParam int quantity, @RequestParam Long userId) {
        cartItemService.addToCart(userId, productId, quantity);  // CartItemService에서 사용자와 상품 처리
        return ResponseEntity.ok("상품이 장바구니에 추가되었습니다.");
    }

    // 장바구니에서 상품 제거
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long productId, @RequestParam Long userId) {
        cartItemService.removeFromCart(userId, productId);  // CartItemService에서 사용자와 상품 처리
        return ResponseEntity.ok("상품이 장바구니에서 제거되었습니다.");
    }
}
