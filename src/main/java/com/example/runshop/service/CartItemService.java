package com.example.runshop.service;

import com.example.runshop.exception.cart.CartItemNotFoundException;
import com.example.runshop.model.entity.CartItem;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    public CartItem addToCart(Long userId, Long productId, int quantity) {
        // 사용자와 상품 조회
        User user = userService.findById(userId);
        Product product = productService.findProductOrThrow(productId);

        // 장바구니에 해당 상품이 이미 있는지 확인
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartItemOpt.isPresent()) {
            // 기존 항목이 있으면 수량 증가
            CartItem existingCartItem = existingCartItemOpt.get();
            existingCartItem.increaseQuantity(quantity);
            return cartItemRepository.save(existingCartItem);
        } else {
            // 새로운 장바구니 항목 생성
            CartItem newCartItem = CartItem.createCartItem(user, product, quantity);
            return cartItemRepository.save(newCartItem);
        }
    }
    @CacheEvict(value = "cartItemsCache", key = "#userId")
    public void removeFromCart(Long userId, Long productId) {
        User user = userService.findUserOrThrow(userId, "Remove from Cart");
        Product product = productService.findProductOrThrow(productId);

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new CartItemNotFoundException("장바구니에 존재하지 않는 상품입니다."));

        cartItemRepository.delete(cartItem);
    }


    public Page<CartItem> getCartItems(Long userId, Pageable pageable) {
        User user = userService.findById(userId);
        return cartItemRepository.findByUser(user, pageable);
    }
}
