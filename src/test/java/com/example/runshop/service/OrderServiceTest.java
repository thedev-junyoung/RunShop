package com.example.runshop.service;

import com.example.runshop.exception.order.OrderAlreadyBeenCancelledException;
import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.dto.payment.OrderRequest;
import com.example.runshop.model.entity.Order;
import com.example.runshop.model.entity.OrderItem;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.model.vo.orderitem.OrderQuantity;
import com.example.runshop.model.vo.payment.PaymentAmount;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.repository.OrderRepository;
import com.example.runshop.utils.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    private User user;
    private Order order;
    private OrderItem orderItem1; // 추가
    private OrderItem orderItem2; // 추가

    private Product product1; // Product 추가
    private Product product2; // Product 추가

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Given: 사용자 설정
        user = new User();
        user.setId(1L);
        user.setName("테스트 유저");

        // Given: 두 개의 Product 설정
        product1 = new Product();
        product1.setId(1L);
        product1.setName(new ProductName("상품1"));

        product2 = new Product();
        product2.setId(2L);
        product2.setName(new ProductName("상품2"));

        // Given: 두 개의 OrderItem 설정
        orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setProduct(product1); // Product 설정
        orderItem1.setQuantity(new OrderQuantity(2));

        orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setProduct(product2); // Product 설정
        orderItem2.setQuantity(new OrderQuantity(1));

        // Given: 주문 설정
        order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(10000.0));
        order.setId(1L);
        order.setOrderItems(List.of(orderItem1, orderItem2)); // OrderItems 설정
    }

    @Test
    @DisplayName("주문을 성공적으로 생성한다")
    public void createOrderSuccessfully() {
        // Given: 사용자 및 OrderRequest 설정
        when(userService.findUserOrThrow(anyLong(), anyString())).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);  // 저장 시 ID 설정
            return savedOrder;
        });

        // InventoryService에서 재고 감소를 모킹 (Product ID 전달)
        doNothing().when(inventoryService).decreaseStock(eq(product1.getId()), anyInt());
        doNothing().when(inventoryService).decreaseStock(eq(product2.getId()), anyInt());

        // 주문 요청 객체 생성 (OrderRequest)
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(user.getId());
        orderRequest.setAmount(new PaymentAmount(BigDecimal.valueOf(10000.0)));
        orderRequest.setOrderItems(List.of(orderItem1, orderItem2)); // 예시로 두 개의 주문 아이템 설정

        // When: 주문 생성
        orderService.createOrder(orderRequest.getUserId(), orderRequest.getAmount().value(), orderRequest.getOrderItems());

        // Then: 주문이 정상적으로 생성되었는지 검증
        verify(orderRepository, times(1)).save(any(Order.class));
        // 실제 Product ID를 사용하여 reduceStock 호출 검증
        verify(inventoryService, times(1)).decreaseStock(eq(product1.getId()), eq(orderItem1.getQuantity().value()));
        verify(inventoryService, times(1)).decreaseStock(eq(product2.getId()), eq(orderItem2.getQuantity().value()));
        verify(userService, times(1)).findUserOrThrow(anyLong(), anyString());
    }

    @Test
    @DisplayName("주문 목록을 성공적으로 페이징하여 조회한다")
    public void getPagedOrderListSuccessfully() {
        // Given: 사용자의 주문 목록을 페이징된 형태로 반환하도록 설정
        Pageable pageable = PageRequest.of(0, 10); // 0번째 페이지, 페이지 당 10개 항목
        List<Order> orders = List.of(order);
        Page<Order> pagedOrders = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findByUserId(anyLong(), eq(pageable))).thenReturn(pagedOrders);
        when(orderMapper.toOrderListDTO(any(Order.class))).thenReturn(
                new OrderListDTO(order.getId(), order.getOrderDate(), order.getStatus(), order.getTotalPrice())
        );

        // When: 주문 목록 페이징 조회
        Page<OrderListDTO> orderList = orderService.getOrderList(user.getId(), pageable);

        // Then: 주문 목록이 정상적으로 페이징 처리되었는지 검증
        assertNotNull(orderList);
        assertEquals(1, orderList.getContent().size());
        assertEquals(order.getId(), orderList.getContent().get(0).getOrderId());
        verify(orderRepository, times(1)).findByUserId(anyLong(), eq(pageable));
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
        OrderAlreadyBeenCancelledException exception = assertThrows(OrderAlreadyBeenCancelledException.class, () -> orderService.cancelOrder(order.getId()));

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

