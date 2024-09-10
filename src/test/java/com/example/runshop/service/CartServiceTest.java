package com.example.runshop.service;


import com.example.runshop.model.entity.Cart;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CartServiceTest {
    // TODO: CartService 테스트 코드 작성
    // 1. 장바구니에 상품 추가
    // 2. 장바구니에 상품 추가 실패 (상품이 존재하지 않음)
    // 3. 장바구니에 상품 추가 실패 (상품 재고 부족)
    // 4. 장바구니 상품 수량 변경
    // 5. 장바구니 상품 수량 변경 실패 (상품이 존재하지 않음)
    // 6. 장바구니 상품 수량 변경 실패 (상품 재고 부족)
    // 7. 장바구니 상품 삭제
    // 8. 장바구니 상품 삭제 실패 (상품이 존재하지 않음)
    // 9. 장바구니 상품 삭제 실패 (상품 재고 부족)
    // 10. 장바구니 비우기
    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock User 생성
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test")
                .name("test")
                .phone("01012345678")
                .address("서울시 강남구")
                .role(UserRole.CUSTOMER)
                .build();

        cart = new Cart();  // Mock Cart 생성
        cart.setId(1L);
        cart.setUser(user);
    }

    @Test
    void testGetCartByUserId() {
        when(cartRepository.findByUserId(1L)).thenReturn(cart);

        Cart result = cartService.getCartByUserId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUser().getId());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    private class CartRepository {
        public Cart findByUserId(long l) {
            return null;
        }
    }

    private class CartService {
        public Cart getCartByUserId(long l) {
            return null;
        }
    }
}
