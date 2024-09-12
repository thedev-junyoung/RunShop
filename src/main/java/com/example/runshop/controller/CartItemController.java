package com.example.runshop.controller;

import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.service.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
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
    @PostMapping("/")
    public ResponseEntity<?> addToCart(@RequestParam Long productId, @RequestParam int quantity, @RequestParam Long userId, HttpServletRequest httpRequest) {
        cartItemService.addToCart(userId, productId, quantity);  // CartItemService에서 사용자와 상품 처리
        return SuccessResponse.ok("상품이 장바구니에 추가되었습니다.",httpRequest.getRequestURI());
    }

    @GetMapping("/")
    public ResponseEntity<?> getCartItems(@RequestParam Long userId, HttpServletRequest httpRequest) {
        return SuccessResponse.ok("장바구니에 담긴 상품을 조회했습니다.", cartItemService.getCartItems(userId), httpRequest.getRequestURI());
    }

    // 장바구니에서 상품 제거
    @DeleteMapping("/")
    public ResponseEntity<?> removeFromCart(@RequestParam Long productId, @RequestParam Long userId, HttpServletRequest httpRequest) {
        cartItemService.removeFromCart(userId, productId);  // CartItemService에서 사용자와 상품 처리
        return SuccessResponse.ok("상품이 장바구니에서 제거되었습니다.", httpRequest.getRequestURI());
    }
}
