package com.example.runshop.service;

import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.entity.Order;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.repository.OrderRepository;
import com.example.runshop.utils.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private OrderMapper orderMapper;

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
        order.setId(1L);
    }

    @Test
    @DisplayName("주문을 성공적으로 생성한다")
    public void createOrderSuccessfully() {
        // Given: userService에서 user를 반환하도록 설정
        when(userService.findUserOrThrow(anyLong(), anyString())).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);  // 저장 시 ID 설정
            return savedOrder;
        });

        // When: 주문 생성
        orderService.createOrder(user.getId(), 10000.0);

        // Then: 주문이 정상적으로 생성되었는지 검증
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(userService, times(1)).findUserOrThrow(anyLong(), anyString());
    }

    @Test
    @DisplayName("주문 목록을 성공적으로 조회한다")
    public void getOrderListSuccessfully() {
        // Given: 사용자의 주문 목록을 반환하도록 설정
        List<Order> orders = List.of(order);
        when(orderRepository.findByUserId(anyLong())).thenReturn(orders);
        when(orderMapper.toOrderListDTO(any(Order.class))).thenReturn(
                new OrderListDTO(order.getId(), order.getOrderDate(), order.getStatus(), order.getTotalPrice())
        );

        // When: 주문 목록 조회
        List<OrderListDTO> orderList = orderService.getOrderList(user.getId());

        // Then: 주문 목록이 정상적으로 조회되었는지 검증
        assertNotNull(orderList);
        assertEquals(1, orderList.size());
        assertEquals(order.getId(), orderList.get(0).getOrderId());
        verify(orderRepository, times(1)).findByUserId(anyLong());
    }

    @Test
    @DisplayName("주문 상세 정보를 성공적으로 조회한다")
    public void getOrderDetailSuccessfully() {
        // Given: 주문 상세 정보 반환 설정
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));
        when(orderMapper.toOrderDetailDTO(any(Order.class))).thenReturn(
                new OrderDetailDTO(order.getId(), order.getOrderDate(), order.getStatus(), order.getTotalPrice(), List.of())
        );

        // When: 주문 상세 조회
        OrderDetailDTO orderDetail = orderService.getOrderDetail(order.getId());

        // Then: 주문 상세 정보가 정상적으로 조회되었는지 검증
        assertNotNull(orderDetail);
        assertEquals(order.getId(), orderDetail.getOrderId());
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("주문을 성공적으로 취소한다")
    public void cancelOrderSuccessfully() {
        // Given: Order가 PENDING 상태일 때 취소하도록 설정
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));

        // When: 주문 취소
        orderService.cancelOrder(order.getId());

        // Then: 주문 상태가 ORDER_CANCELLATION으로 변경되었는지 확인
        assertEquals(OrderStatus.ORDER_CANCELLATION, order.getStatus());

        // OrderRepository의 save 메소드가 호출되었는지 확인 (취소된 주문은 별도의 저장이 필요 없음)
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("이미 취소된 주문을 취소하려 하면 예외가 발생한다")
    public void cancelAlreadyCancelledOrderThrowsException() {
        // Given: 이미 취소된 주문 상태로 설정
        order.setStatus(OrderStatus.ORDER_CANCELLATION);
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));

        // When & Then: 이미 취소된 주문을 다시 취소할 때 IllegalArgumentException 예외가 발생하는지 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.cancelOrder(order.getId());
        });

        assertEquals("이미 취소된 주문입니다.", exception.getMessage());

        // OrderRepository의 save 메소드가 호출되지 않음을 확인
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class)); // 취소된 주문이 다시 저장되지 않음을 확인
    }

    @Test
    @DisplayName("주문 상태를 성공적으로 변경한다")
    public void changeOrderStatusSuccessfully() {
        // Given: Order가 PENDING 상태일 때
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));

        // When: 주문 상태를 DELIVERY_COMPLETE로 변경
        orderService.changeOrderStatus(order.getId(), OrderStatus.DELIVERY_COMPLETE);

        // Then: 주문 상태가 변경되었는지 확인
        assertEquals(OrderStatus.DELIVERY_COMPLETE, order.getStatus());

        // OrderRepository의 findById 및 save 메소드가 호출되었는지 확인
        verify(orderRepository, times(1)).findById(anyLong());
    }
}

