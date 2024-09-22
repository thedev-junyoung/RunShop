package com.example.runshop.service;

import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.model.vo.orderitem.OrderQuantity;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.module.order.application.service.OrderService;
import com.example.runshop.module.order.domain.Order;
import com.example.runshop.module.order.domain.OrderItem;
import com.example.runshop.module.order.application.port.out.OrderRepository;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.utils.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ApplicationEventPublisher eventPublisher; // 이벤트 발행을 위한 Mock 추가

    private User user;
    private Order order;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("테스트 유저");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName(new ProductName("상품1"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName(new ProductName("상품2"));

        orderItem1 = new OrderItem();
        orderItem1.setProduct(product1);
        orderItem1.setQuantity(new OrderQuantity(2));

        orderItem2 = new OrderItem();
        orderItem2.setProduct(product2);
        orderItem2.setQuantity(new OrderQuantity(1));

        order = new Order(user, new BigDecimal("10000.0"), List.of(orderItem1, orderItem2));
    }

    @Test
    @DisplayName("주문을 성공적으로 생성하고 결제 이벤트를 발행한다")
    public void createOrderAndTriggerPaymentEventSuccessfully() {
        when(userService.findUserOrThrow(anyLong(), anyString())).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        doNothing().when(inventoryService).decreaseStock(anyLong(), anyInt());

        // When: 주문 생성
        orderService.createOrder(user.getId(), new BigDecimal("10000.0"), List.of(orderItem1, orderItem2), PaymentMethod.CREDIT_CARD);

        // Then: 주문이 정상적으로 생성되고 결제 이벤트가 발행되었는지 확인
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publishEvent(any(PaymentRequestEvent.class));  // 결제 요청 이벤트가 발행되었는지 확인
    }

    @Test
    @DisplayName("주문 목록을 페이징 처리하여 조회한다")
    public void getPagedOrderListSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = List.of(order);
        Page<Order> pagedOrders = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findByUserId(anyLong(), eq(pageable))).thenReturn(pagedOrders);
        when(orderMapper.toOrderListDTO(any(Order.class))).thenReturn(
                new OrderListDTO(order.getId(), order.getOrderDate(), order.getStatus(), order.getTotalPrice())
        );

        Page<OrderListDTO> orderList = orderService.getOrderList(user.getId(), pageable);

        assertNotNull(orderList);
        assertEquals(1, orderList.getContent().size());
        verify(orderRepository, times(1)).findByUserId(anyLong(), eq(pageable));
    }

    @Test
    @DisplayName("주문을 성공적으로 취소하고 재고를 복구한다")
    public void cancelOrderSuccessfully() {
        // Given: 주문 객체에 ID 설정
        order.setId(1L);  // ID가 설정되지 않아 발생한 문제 해결

        // 주문을 찾을 수 있도록 Mock 설정
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));

        // When: 주문 취소
        orderService.cancelOrder(order.getId());

        // Then: 주문이 정상적으로 취소되었는지 확인
        assertEquals(OrderStatus.ORDER_CANCELLATION, order.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());
        verify(inventoryService, times(2)).increaseStock(anyLong(), anyInt());  // 각 상품별로 재고 복구 호출 확인
    }
}
