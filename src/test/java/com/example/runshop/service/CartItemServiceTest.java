package com.example.runshop.service;

import com.example.runshop.model.entity.CartItem;
import com.example.runshop.model.entity.Inventory;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.enums.UserRole;
import com.example.runshop.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @InjectMocks
    private CartItemService cartItemService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

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
        inventory.setStockQuantity(10);
        product.setInventory(inventory);
    }

    @Test
    @DisplayName("상품을 장바구니에 추가하면 CartItem이 생성된다.")
    public void whenAddValidProductToCart_thenCartItemCreated() {
        // 필요한 곳에만 stubbing 적용
        when(userService.findById(anyLong())).thenReturn(user);
        when(productService.findById(anyLong())).thenReturn(product);
        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartItem cartItem = cartItemService.addToCart(user.getId(), product.getId(), 1);

        assertNotNull(cartItem);
        assertEquals(1, cartItem.getQuantity());
        assertEquals(product.getId(), cartItem.getProduct().getId());
        user.getCartItems().add(cartItem);

        assertTrue(user.getCartItems().contains(cartItem));
    }

    @Test
    @DisplayName("상품이 이미 장바구니에 존재할 경우 수량이 증가된다.")
    public void whenProductAlreadyInCart_thenQuantityIncreases() {
        // Given: 장바구니에 이미 상품이 추가된 상태
        CartItem existingCartItem = new CartItem();
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(1);
        user.getCartItems().add(existingCartItem);

        when(userService.findById(anyLong())).thenReturn(user);
        when(productService.findById(anyLong())).thenReturn(product);
        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(existingCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: 수량을 2만큼 추가
        CartItem updatedCartItem = cartItemService.addToCart(user.getId(), product.getId(), 2);

        // Then: 수량이 3으로 증가했는지 확인
        assertNotNull(updatedCartItem);
        assertEquals(3, updatedCartItem.getQuantity());
        assertEquals(product.getId(), updatedCartItem.getProduct().getId());
    }
    @Test
    @DisplayName("장바구니에서 상품을 성공적으로 삭제할 수 있다.")
    public void whenRemoveProductFromCart_thenCartItemRemoved() {
        // Given: 장바구니에 상품이 추가된 상태
        CartItem existingCartItem = new CartItem();
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(1);
        user.getCartItems().add(existingCartItem);

        when(userService.findById(anyLong())).thenReturn(user);
        when(productService.findById(anyLong())).thenReturn(product);
        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(existingCartItem));

        cartItemService.removeFromCart(user.getId(), product.getId());

        verify(cartItemRepository, times(1)).delete(existingCartItem);
    }

    @Test
    @DisplayName("장바구니에 없는 상품을 삭제하려 하면 예외가 발생한다.")
    public void whenRemoveNonExistentProductFromCart_thenThrowException() {
        when(userService.findById(anyLong())).thenReturn(user);
        when(productService.findById(anyLong())).thenReturn(product);
        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartItemService.removeFromCart(user.getId(), product.getId());
        });

        assertEquals("장바구니에 존재하지 않는 상품입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("장바구니를 조회하면 해당 사용자의 장바구니 목록을 반환한다.")
    public void whenViewCart_thenReturnCartItems() {
        // Given: 장바구니에 상품이 추가된 상태
        CartItem cartItem1 = new CartItem();
        cartItem1.setProduct(product);
        cartItem1.setQuantity(1);
        user.getCartItems().add(cartItem1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setProduct(product);
        cartItem2.setQuantity(2);
        user.getCartItems().add(cartItem2);

        when(userService.findById(anyLong())).thenReturn(user);
        when(cartItemRepository.findByUser(user)).thenReturn(user.getCartItems());

        // When: 장바구니 조회
        var cartItems = cartItemService.getCartItems(user.getId());

        // Then: 장바구니 목록이 반환되는지 확인
        assertNotNull(cartItems);
        assertEquals(2, cartItems.size());
        assertTrue(cartItems.contains(cartItem1));
        assertTrue(cartItems.contains(cartItem2));
    }
}
