package com.example.runshop.controller;

import com.example.runshop.model.dto.cart.CartItemRequest;
import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.service.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest cartItemRequest, HttpServletRequest httpRequest) {
        cartItemService.addToCart(cartItemRequest.getUserId(), cartItemRequest.getProductId(), cartItemRequest.getQuantity());
        return SuccessResponse.ok("상품이 장바구니에 추가되었습니다.", httpRequest.getRequestURI());
    }

    // 장바구니 조회
    @GetMapping("/")
    public ResponseEntity<?> getCartItems(@RequestParam Long userId, Pageable pageable, HttpServletRequest httpRequest) {
        return SuccessResponse.ok("장바구니에 담긴 상품을 조회했습니다.", cartItemService.getCartItems(userId, pageable), httpRequest.getRequestURI());
    }

    // 장바구니에서 상품 제거
    @DeleteMapping("/")
    public ResponseEntity<?> removeFromCart(@RequestBody CartItemRequest cartItemRequest, HttpServletRequest httpRequest) {
        cartItemService.removeFromCart(cartItemRequest.getUserId(), cartItemRequest.getProductId());
        return SuccessResponse.ok("상품이 장바구니에서 제거되었습니다.", httpRequest.getRequestURI());
    }
}
