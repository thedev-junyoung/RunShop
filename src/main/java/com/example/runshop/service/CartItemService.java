package com.example.runshop.service;

import com.example.runshop.config.RoleCheck;
import com.example.runshop.exception.cart.CartItemNotFoundException;
import com.example.runshop.model.entity.CartItem;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.repository.CartItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    public CartItemService(CartItemRepository cartItemRepository, UserService userService, ProductService productService) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @RoleCheck("CUSTOMER")
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        // 사용자와 상품 조회
        User user = userService.findById(userId);
        Product product = productService.findProductOrThrow(productId);

        // 장바구니에 해당 상품이 이미 있는지 확인
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByUserAndProduct(user, product);


        if (existingCartItemOpt.isPresent()) {
            // 이미 존재하는 CartItem이 있을 경우 수량 증가
            return IncreaseQuantityCartItemAlreadyExists(quantity, existingCartItemOpt);
        } else {
            // 없을 경우 새로운 CartItem을 생성
            return NotPresentCreateCartItem(quantity, user, product);
        }
    }

    private CartItem IncreaseQuantityCartItemAlreadyExists(int quantity, Optional<CartItem> existingCartItemOpt) {
        CartItem existingCartItem = existingCartItemOpt.get();
        existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        return cartItemRepository.save(existingCartItem);
    }
    private CartItem NotPresentCreateCartItem(int quantity, User user, Product product) {
        CartItem newCartItem = new CartItem();
        newCartItem.setUser(user);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        return cartItemRepository.save(newCartItem);
    }
    @RoleCheck("CUSTOMER")
    @CacheEvict(value = "cartItemsCache", key = "#userId")  // 해당 사용자의 장바구니 캐시 무효화
    public void removeFromCart(Long userId, Long productId) {
        User user = userService.findUserOrThrow(userId, "Remove from Cart");
        Product product = productService.findProductOrThrow(productId);

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new CartItemNotFoundException("장바구니에 존재하지 않는 상품입니다."));

        cartItemRepository.delete(cartItem);
    }

    @Cacheable(value = "cartItemsCache", key = "#userId")
    public List<CartItem> getCartItems(Long userId) {
        User user = userService.findById(userId);
        return cartItemRepository.findByUser(user);
    }
}
