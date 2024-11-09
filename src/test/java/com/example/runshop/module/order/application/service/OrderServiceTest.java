package com.example.runshop.module.order.application.service;

import com.example.runshop.exception.order.OrderNotFoundException;
import com.example.runshop.model.dto.order.OrderDetailDTO;
import com.example.runshop.model.dto.order.OrderListDTO;
import com.example.runshop.model.entity.Inventory;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.Seller;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.enums.UserRole;
import com.example.runshop.model.vo.orderitem.OrderQuantity;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import com.example.runshop.module.order.application.port.out.OrderRepository;
import com.example.runshop.module.order.domain.Order;
import com.example.runshop.module.order.domain.OrderItem;
import com.example.runshop.model.entity.User;
import com.example.runshop.module.payment.adapters.in.event.PaymentFailedEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentSuccessEvent;
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.service.UserService;
import com.example.runshop.utils.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Order order;
    private List<OrderItem> orderItems;
    private BigDecimal totalPrice;
    private PaymentMethod paymentMethod;
    private Product product;
    @Mock
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email(new com.example.runshop.model.vo.user.Email("test@example.com"))
                .password(new com.example.runshop.model.vo.user.Password("encodedPassword"))
                .name("테스트 사용자")
                .phone("010-1234-5678")
                .address(new com.example.runshop.model.vo.user.Address("서울시", "강남구", "테스트로 123", "서울", "12345"))
                .role(UserRole.CUSTOMER)
                .enabled(true)
                .build();

        product = new Product(
                new ProductName("product"),
                new ProductDescription("description"),
                new ProductPrice(BigDecimal.valueOf(100.00)),
                Category.TOP,
                "나이키",
                new Seller(),
                inventory
        );

        order = mock(Order.class); // Order 객체를 목(mock)으로 생성

        orderItems = List.of(OrderItem.create(
            product,new OrderQuantity(3),PaymentMethod.CREDIT_CARD));
        totalPrice = BigDecimal.valueOf(100.00);
        paymentMethod = PaymentMethod.CREDIT_CARD;
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() {
        when(userService.findUserOrThrow(anyLong(), anyString())).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.createOrder(1L, totalPrice, orderItems, paymentMethod);

        verify(userService, times(1)).findUserOrThrow(anyLong(), anyString());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publishEvent(any(PaymentRequestEvent.class));
    }

    @Test
    @DisplayName("결제 성공 이벤트 처리")
    void handlePaymentSuccess() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        PaymentSuccessEvent event = new PaymentSuccessEvent(this, 1L);
        orderService.handlePaymentSuccess(event);

        verify(orderRepository, times(1)).findById(anyLong());
        verify(order, times(1)).completePayment();
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("결제 실패 이벤트 처리")
    void handlePaymentFailure() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        PaymentFailedEvent event = new PaymentFailedEvent(this, 1L);
        orderService.handlePaymentFailure(event);

        verify(orderRepository, times(1)).findById(anyLong());
        verify(order, times(1)).cancelOrder();
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("주문 목록 조회 성공")
    void getOrderList_Success() {
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findByUserId(anyLong(), any(PageRequest.class))).thenReturn(orderPage);
        when(orderMapper.toOrderListDTO(any(Order.class))).thenReturn(new OrderListDTO());

        Page<OrderListDTO> result = orderService.getOrderList(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findByUserId(anyLong(), any(PageRequest.class));
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    void getOrderDetail_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDetailDTO(any(Order.class))).thenReturn(new OrderDetailDTO());

        OrderDetailDTO result = orderService.getOrderDetail(1L);

        assertNotNull(result);
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderMapper, times(1)).toOrderDetailDTO(any(Order.class));
    }

    @Test
    @DisplayName("주문 상세 조회 시 주문을 찾을 수 없는 경우 예외 발생")
    void getOrderDetail_OrderNotFoundException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderDetail(1L));

        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("주문 취소 성공")
    void cancelOrder_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        verify(orderRepository, times(1)).findById(anyLong());
        verify(order, times(1)).cancelOrder();
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("주문 취소 시 주문을 찾을 수 없는 경우 예외 발생")
    void cancelOrder_OrderNotFoundException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(1L));

        verify(orderRepository, times(1)).findById(anyLong());
        verify(order, never()).cancelOrder();
    }
}
