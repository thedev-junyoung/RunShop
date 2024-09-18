package com.example.runshop.service;

import com.example.runshop.gateway.PaymentGateway;
import com.example.runshop.model.dto.payment.OrderRequest;
import com.example.runshop.model.entity.Order;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.model.enums.PaymentMethod;
import com.example.runshop.model.vo.payment.PaymentAmount;
import com.example.runshop.repository.OrderRepository;
import com.example.runshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository; // PaymentRepository에 대한 Mock 추가

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private OrderService orderService;  // OrderService에 대한 Mock 추가

    @InjectMocks
    private PaymentService paymentService;

    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Given: 주문 설정
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(10000));

        // OrderRequest 설정
        orderRequest = new OrderRequest();
        orderRequest.setPaymentMethod(PaymentMethod.CARD);
        orderRequest.setAmount(new PaymentAmount(BigDecimal.valueOf(10000)));
    }

    @Test
    @DisplayName("결제가 성공하면 주문 상태가 PAYMENT_COMPLETE로 변경된다")
    public void whenPaymentSucceeds_thenOrderStatusChangesToComplete() {
        when(orderService.findOrderOrThrow(anyLong())).thenReturn(order);  // OrderService의 Mock 설정
        when(paymentGateway.processPayment(any(PaymentMethod.class), any(BigDecimal.class))).thenReturn(true);

        // 결제 처리
        paymentService.processPayment(order.getId(), orderRequest);

        // Then: 결제 성공 후 상태 변경 확인
        assertEquals(OrderStatus.PAYMENT_COMPLETE, order.getStatus());
        verify(orderRepository, times(1)).save(order);
        verify(paymentRepository, times(1)).save(any()); // PaymentRepository 호출 확인
    }

    @Test
    @DisplayName("결제가 실패하면 주문 상태가 PENDING으로 유지된다")
    public void whenPaymentFails_thenOrderStatusRemainsPending() {
        when(orderService.findOrderOrThrow(anyLong())).thenReturn(order);  // OrderService의 Mock 설정
        when(paymentGateway.processPayment(any(PaymentMethod.class), any(BigDecimal.class))).thenReturn(false);

        // 결제 처리
        paymentService.processPayment(order.getId(), orderRequest);

        // Then: 결제 실패 후 상태 확인
        assertEquals(OrderStatus.PENDING, order.getStatus());
        verify(orderRepository, times(1)).save(order);
        verify(paymentRepository, times(1)).save(any()); // PaymentRepository 호출 확인
    }
}
