package com.example.runshop.Integration;

import com.example.runshop.exception.cart.CartItemNotFoundException;
import com.example.runshop.model.entity.CartItem;
import com.example.runshop.model.entity.Inventory;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.enums.UserRole;
import com.example.runshop.repository.CartItemRepository;
import com.example.runshop.service.CartItemService;
import com.example.runshop.service.ProductService;
import com.example.runshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class IntegrationCartItemServiceTest {

    @Autowired
    private CartItemService cartItemService;

    @MockBean
    private CartItemRepository cartItemRepository;

    @MockBean
    private UserService userService;  // UserService를 Mocking

    @MockBean
    private ProductService productService;  // ProductService를 Mocking

    private User user;
    private Product product;

    @BeforeEach
    public void setUp() {
        // Given: User 설정
        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("1234");
        user.setName("테스트");
        user.setPhone("010-1234-5678");
        user.setAddress("서울시 강남구");
        user.setRole(UserRole.CUSTOMER);
        user.setCartItems(new ArrayList<>());

        // Given: Product 및 Inventory 설정
        product = new Product();
        product.setId(1L);
        product.setName("테스트 상품");
        product.setDescription("테스트 상품입니다.");
        product.setPrice(10000);
        product.setCategory(Category.TOP);
        product.setBrand("테스트 브랜드");

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setStockQuantity(10); // 재고 설정
        product.setInventory(inventory);

        // UserService의 findById를 Mocking
        when(userService.findById(anyLong())).thenReturn(user);

        // ProductService의 findById를 Mocking
        when(productService.findById(anyLong())).thenReturn(product);

        // Mocking save method
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("상품을 장바구니에 추가하면 CartItem이 생성된다.")
    public void whenAddValidProductToCart_thenCartItemCreated() {
        // Mocking the repository behavior: 장바구니에 동일한 상품이 없다고 가정
        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        // When
        CartItem cartItem = cartItemService.addToCart(user.getId(), product.getId(), 1);

        // Then
        assertNotNull(cartItem);
        assertEquals(1, cartItem.getQuantity());
        assertEquals(product.getId(), cartItem.getProduct().getId());

        // CartItem을 user의 장바구니 리스트에 추가
        user.getCartItems().add(cartItem);

        // 장바구니에 상품이 추가되었는지 확인
        assertTrue(user.getCartItems().contains(cartItem));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("상품이 이미 장바구니에 존재할 경우 수량이 증가된다.")
    public void whenProductAlreadyInCart_thenQuantityIncreases() {
        // Given: 장바구니에 이미 상품이 추가된 상태로 가정
        CartItem existingCartItem = new CartItem();
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(1);  // 이미 1개의 상품이 있음
        user.getCartItems().add(existingCartItem);

        // Mocking the repository behavior: 동일한 상품이 이미 장바구니에 있는 경우
        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(existingCartItem));

        // When: 동일한 상품을 다시 장바구니에 추가
        CartItem updatedCartItem = cartItemService.addToCart(user.getId(), product.getId(), 2);  // 수량 2 추가

        // Then: 수량이 증가해야 함
        assertNotNull(updatedCartItem);
        assertEquals(3, updatedCartItem.getQuantity());  // 총 3개로 증가했는지 확인
        assertEquals(product.getId(), updatedCartItem.getProduct().getId());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("장바구니에서 상품을 성공적으로 삭제할 수 있다.")
    public void whenRemoveProductFromCart_thenCartItemRemoved() {
        // Given: 장바구니에 상품이 추가된 상태
        CartItem existingCartItem = new CartItem();
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(1);
        user.getCartItems().add(existingCartItem);

        // findUserOrThrow 메서드를 사용한 stubbing
        when(userService.findUserOrThrow(anyLong(), anyString())).thenReturn(user);
        when(productService.findById(anyLong())).thenReturn(product);

        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(existingCartItem));

        // When: 장바구니에서 해당 상품을 삭제
        cartItemService.removeFromCart(user.getId(), product.getId());

        // Then: 삭제된 것이 맞는지 확인
        verify(cartItemRepository, times(1)).delete(existingCartItem);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("장바구니에 없는 상품을 삭제하려 하면 예외가 발생한다.")
    public void whenRemoveNonExistentProductFromCart_thenThrowException() {
        // findUserOrThrow 메서드를 사용한 stubbing
        when(userService.findUserOrThrow(anyLong(), anyString())).thenReturn(user);
        when(productService.findById(anyLong())).thenReturn(product);
        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        // When & Then: 장바구니에 없는 상품을 삭제하려 할 때 예외가 발생해야 함
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartItemService.removeFromCart(user.getId(), product.getId());
        });

        assertEquals("장바구니에 존재하지 않는 상품입니다.", exception.getMessage());
    }
}
