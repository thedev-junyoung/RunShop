package com.example.runshop.module.payment.application.service;

import com.example.runshop.exception.order.OrderNotFoundException;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.module.order.application.port.out.OrderRepository;
import com.example.runshop.module.order.domain.Order;
import com.example.runshop.module.payment.adapters.in.event.PaymentFailedEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentSuccessEvent;
import com.example.runshop.module.payment.application.port.out.PaymentRepository;
import com.example.runshop.module.payment.domain.Payment;
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.module.payment.intrastructure.gateway.external.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentGateway paymentGateway;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private Order order;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        lenient().when(order.getId()).thenReturn(1L);
        lenient().when(order.getStatus()).thenReturn(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("결제 성공 시 PaymentSuccessEvent 발행")
    void processPayment_Success() {
        PaymentRequestEvent event = new PaymentRequestEvent(this, 1L, BigDecimal.valueOf(100.00), PaymentMethod.CREDIT_CARD);

        when(paymentGateway.process(any(), any())).thenReturn(true);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        paymentService.processPayment(event);

        verify(paymentGateway, times(1)).process(any(), any());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(eventPublisher, times(1)).publishEvent(any(PaymentSuccessEvent.class));
    }

    @Test
    @DisplayName("결제 실패 시 PaymentFailedEvent 발행")
    void processPayment_Failure() {
        PaymentRequestEvent event = new PaymentRequestEvent(this, 1L, BigDecimal.valueOf(100.00), PaymentMethod.CREDIT_CARD);

        when(paymentGateway.process(any(), any())).thenReturn(false);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        paymentService.processPayment(event);

        verify(paymentGateway, times(1)).process(any(), any());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(eventPublisher, times(1)).publishEvent(any(PaymentFailedEvent.class));
    }

    @Test
    @DisplayName("이미 결제된 주문은 중복 결제하지 않음")
    void processPayment_AlreadyPaid() {
        PaymentRequestEvent event = new PaymentRequestEvent(this, 1L, BigDecimal.valueOf(100.00), PaymentMethod.CREDIT_CARD);

        when(order.getStatus()).thenReturn(OrderStatus.PAYMENT_COMPLETE);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        paymentService.processPayment(event);

        verify(paymentGateway, never()).process(any(), any());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("주문을 찾을 수 없을 때 OrderNotFoundException 발생")
    void processPayment_OrderNotFoundException() {
        PaymentRequestEvent event = new PaymentRequestEvent(this, 1L, BigDecimal.valueOf(100.00), PaymentMethod.CREDIT_CARD);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> paymentService.processPayment(event));

        verify(paymentGateway, never()).process(any(), any());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
}
