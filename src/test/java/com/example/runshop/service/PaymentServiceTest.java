package com.example.runshop.service;

import com.example.runshop.exception.order.OrderNotFoundException;
import com.example.runshop.exception.payment.InvalidPaymentAmountException;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import com.example.runshop.module.order.domain.OrderItem;
import com.example.runshop.module.payment.adapters.in.event.PaymentFailedEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentSuccessEvent;
import com.example.runshop.module.payment.application.port.out.PaymentRepository;
import com.example.runshop.module.payment.application.service.PaymentService;
import com.example.runshop.module.order.domain.Order;
import com.example.runshop.module.order.application.port.out.OrderRepository;
import com.example.runshop.module.payment.domain.Payment;
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.model.vo.payment.PaymentAmount;
import com.example.runshop.module.payment.intrastructure.gateway.external.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;  // 이벤트 발행을 위한 Mock

    @Mock
    private PaymentGateway paymentGateway;  // PaymentGateway에 대한 Mock 추가

    @InjectMocks
    private PaymentService paymentService;

    private Order order;
    private PaymentAmount paymentAmount;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Given: User 생성
        User user = new User();
        user.setId(1L);
        user.setName("테스트 유저");

        // Given: OrderItem 리스트 생성
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(Product.builder()
                .id(1L)
                .name(new ProductName("Product1"))
                .description(new ProductDescription("Product1 Description"))
                .price(new ProductPrice(BigDecimal.valueOf(5000)))
                .category(Category.TOP)
                .brand("Brand1")
                .enabled(true)
                .build()
        ); // 가정된 Product 생성

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(Product.builder()
                .id(2L)
                .name(new ProductName("Product2"))
                .description(new ProductDescription("Product2 Description"))
                .price(new ProductPrice(BigDecimal.valueOf(5000)))
                .category(Category.TOP)
                .brand("Brand2")
                .enabled(true)
                .build()
        ); // 가정된 Product 생성
        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        // Given: Order 생성 (User, TotalPrice, OrderItems)
        order = new Order(user, BigDecimal.valueOf(10000), orderItems);
        order.setId(1L);  // 주문 객체에 ID 설정

        // 결제 금액 설정
        paymentAmount = new PaymentAmount(BigDecimal.valueOf(10000));
    }

    @Test
    @DisplayName("결제가 성공하면 주문 상태가 PAYMENT_COMPLETE로 변경되고 성공 이벤트가 발행된다")
    public void whenPaymentSucceeds_thenOrderStatusChangesToComplete() {
        // Given: 주문이 성공적으로 조회되고 Payment 저장 준비
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock external payment processing to return success
        when(paymentGateway.process(any(PaymentMethod.class), any(BigDecimal.class))).thenReturn(true);

        // When: 결제 처리 (결제 수단을 CREDIT_CARD로 설정)
        paymentService.processPayment(new PaymentRequestEvent(this, order.getId(), paymentAmount.value(), PaymentMethod.CREDIT_CARD));

        // Then: 결제가 성공했을 때 주문 상태가 PAYMENT_COMPLETE로 변경되었는지 확인
        assertEquals(OrderStatus.PAYMENT_COMPLETE, order.getStatus());

        // PaymentRepository가 호출되었는지 확인
        verify(paymentRepository, times(1)).save(any(Payment.class));  // save가 한 번만 호출되었는지 확인

        // 결제 성공 이벤트가 발행되었는지 확인
        verify(eventPublisher, times(1)).publishEvent(any(PaymentSuccessEvent.class));
    }
    @Test
    @DisplayName("결제가 실패하면 주문 상태가 PENDING으로 유지되고 실패 이벤트가 발행된다")
    public void whenPaymentFails_thenOrderStatusRemainsPending() {
        // Given: 주문이 성공적으로 조회되고 Payment 저장 준비
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock external payment processing to return failure
        when(paymentGateway.process(any(PaymentMethod.class), any(BigDecimal.class))).thenReturn(false);

        // When: 결제 처리
        paymentService.processPayment(new PaymentRequestEvent(this, order.getId(), paymentAmount.value(), PaymentMethod.CREDIT_CARD));

        // Then: 결제가 실패했을 때 주문 상태가 PENDING으로 유지되는지 확인
        assertEquals(OrderStatus.PENDING, order.getStatus());

        // PaymentRepository가 호출되었는지 확인
        verify(paymentRepository, times(1)).save(any(Payment.class));  // save가 한 번만 호출되었는지 확인

        // 결제 실패 이벤트가 발행되었는지 확인
        verify(eventPublisher, times(1)).publishEvent(any(PaymentFailedEvent.class));
    }
    @Test
    @DisplayName("결제 금액이 0이거나 음수일 경우 결제가 실패해야 한다")
    public void whenPaymentAmountIsZeroOrNegative_thenPaymentFails() {
        // 결제 금액이 0인 경우를 테스트
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // PaymentAmount 객체 생성 시 예외가 발생하는지 확인
        assertThrows(InvalidPaymentAmountException.class, () -> {
            new PaymentAmount(zeroAmount);  // 여기서 예외 발생
        });

        // 결제 금액이 음수인 경우도 테스트
        BigDecimal negativeAmount = BigDecimal.valueOf(-1000);

        // 음수 금액일 때도 예외가 발생하는지 확인
        assertThrows(InvalidPaymentAmountException.class, () -> {
            new PaymentAmount(negativeAmount);  // 여기서 예외 발생
        });
    }
    @Test
    @DisplayName("이미 결제된 주문에 대해서는 결제 요청이 처리되지 않는다")
    public void whenOrderAlreadyPaid_thenPaymentIsNotProcessed() {
        // Given: 주문 상태가 PAYMENT_COMPLETE로 설정된 경우
        order.setStatus(OrderStatus.PAYMENT_COMPLETE);

        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));

        // When: 결제 요청
        paymentService.processPayment(new PaymentRequestEvent(this, order.getId(), paymentAmount.value(), PaymentMethod.CREDIT_CARD));

        // Then: 결제 처리가 되지 않았는지 확인
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishEvent(any(PaymentSuccessEvent.class));
        verify(eventPublisher, never()).publishEvent(any(PaymentFailedEvent.class));
    }
    @Test
    @DisplayName("결제 요청 시 주문이 존재하지 않으면 예외가 발생한다")
    public void whenOrderDoesNotExist_thenThrowException() {
        // Given: 주문이 존재하지 않는 경우
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        // When & Then: 주문을 찾을 수 없을 때 예외가 발생하는지 확인
        assertThrows(OrderNotFoundException.class, () ->
                paymentService.processPayment(new PaymentRequestEvent(this, 999L, paymentAmount.value(), PaymentMethod.CREDIT_CARD))
        );

        // 결제가 처리되지 않았는지 확인
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishEvent(any(PaymentSuccessEvent.class));
        verify(eventPublisher, never()).publishEvent(any(PaymentFailedEvent.class));
    }

    @Test
    @DisplayName("다양한 결제 수단에 대해 결제가 성공적으로 처리된다")
    public void whenPaymentMethodIsDifferent_thenProcessPaymentSuccessfully() {
        // Given: 결제 수단을 카드 대신 다른 것으로 변경 (예: BANK_TRANSFER)
        PaymentMethod bankTransfer = PaymentMethod.BANK_TRANSFER;

        // Given: 주문이 성공적으로 조회되고 Payment 저장 준비
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock external payment processing to return success for bank transfer
        when(paymentGateway.process(eq(bankTransfer), any(BigDecimal.class))).thenReturn(true);

        // When: 결제 처리 (bankTransfer를 사용하여)
        paymentService.processPayment(new PaymentRequestEvent(this, order.getId(), paymentAmount.value(), bankTransfer));

        // Then: 결제가 성공했을 때 주문 상태가 PAYMENT_COMPLETE로 변경되었는지 확인
        assertEquals(OrderStatus.PAYMENT_COMPLETE, order.getStatus());

        // PaymentRepository가 호출되었는지 확인
        verify(paymentRepository, times(1)).save(any(Payment.class));

        // 결제 성공 이벤트가 발행되었는지 확인
        verify(eventPublisher, times(1)).publishEvent(any(PaymentSuccessEvent.class));
    }
}
