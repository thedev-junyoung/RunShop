package com.example.runshop.service;

import com.example.runshop.model.entity.Order;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserService userService;
    private User user;
    private Order order;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Given: 사용자와 주문 설정
        user = new User();
        user.setId(1L);
        user.setName("테스트 유저");

        order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(10000.0);
    }
    @Test
    @DisplayName("주문을 성공적으로 생성한다")
    public void createOrderSuccessfully() {
        // Given: userService에서 user를 반환하도록 설정
        when(userService.findById(anyLong())).thenReturn(user);

        // Order 객체에 id 값을 설정하여 반환되도록 설정
        order.setId(1L);  // id 값을 설정
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);  // 저장 시 ID 설정
            return savedOrder;
        });
        // When: 주문 생성
        Order createdOrder = orderService.createOrder(user.getId(), 10000.0);

        // Then: 주문이 정상적으로 생성되었는지 검증
        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getId());  // 주문 ID가 null이 아닌지 확인
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertEquals(10000.0, createdOrder.getTotalPrice());
        assertNotNull(createdOrder.getOrderDate());
        assertEquals(user, createdOrder.getUser());

        // OrderRepository의 save 메소드가 호출되었는지 확인
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    @Test
    void cancelOrder() {
    }

    @Test
    void changeOrderStatus() {
    }
}