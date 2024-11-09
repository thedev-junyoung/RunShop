package com.example.runshop.service;

import com.example.runshop.exception.order.OrderNotFoundException;
import com.example.runshop.exception.payment.InvalidPaymentAmountException;
import com.example.runshop.model.entity.Inventory;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.Category;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.model.vo.inventory.StockQuantity;
import com.example.runshop.model.vo.orderitem.OrderQuantity;
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
import org.springframework.test.util.ReflectionTestUtils;

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
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;
    private PaymentAmount paymentAmount;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // User 생성
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);  // User ID 설정
        user.setName("테스트 유저");

        // Product1 생성 및 Inventory 설정
        Product product1 = Product.builder()
                .name(new ProductName("Product1"))
                .description(new ProductDescription("Product1 Description"))
                .price(new ProductPrice(BigDecimal.valueOf(5000)))
                .category(Category.TOP)
                .brand("Brand1")
                .enabled(true)
                .build();
        ReflectionTestUtils.setField(product1, "id", 1L);  // Product1 ID 설정

        Inventory inventory1 = new Inventory(new StockQuantity(10));
        inventory1.setProduct(product1);
        product1.setInventory(inventory1);  // Product1에 Inventory 설정

        // Product2 생성 및 Inventory 설정
        Product product2 = Product.builder()
                .name(new ProductName("Product2"))
                .description(new ProductDescription("Product2 Description"))
                .price(new ProductPrice(BigDecimal.valueOf(5000)))
                .category(Category.TOP)
                .brand("Brand2")
                .enabled(true)
                .build();
        ReflectionTestUtils.setField(product2, "id", 2L);  // Product2 ID 설정

        Inventory inventory2 = new Inventory(new StockQuantity(5));
        inventory2.setProduct(product2);
        product2.setInventory(inventory2);  // Product2에 Inventory 설정

        // OrderItem 생성
        OrderItem orderItem1 = OrderItem.create(product1, new OrderQuantity(2), PaymentMethod.CREDIT_CARD);
        OrderItem orderItem2 = OrderItem.create(product2, new OrderQuantity(1), PaymentMethod.CREDIT_CARD);
        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        // Order 생성 및 ID 설정
        order = Order.create(user, BigDecimal.valueOf(10000), orderItems);
        ReflectionTestUtils.setField(order, "id", 1L);  // Order ID 설정

        // 결제 금액 설정
        paymentAmount = new PaymentAmount(BigDecimal.valueOf(10000));
    }

    @Test
    @DisplayName("결제가 성공하면 주문 상태가 PAYMENT_COMPLETE로 변경되고 성공 이벤트가 발행된다")
    public void whenPaymentSucceeds_thenOrderStatusChangesToComplete() {
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentGateway.process(any(PaymentMethod.class), any(BigDecimal.class))).thenReturn(true);

        paymentService.processPayment(new PaymentRequestEvent(this, order.getId(), paymentAmount.value(), PaymentMethod.CREDIT_CARD));

        assertEquals(OrderStatus.PAYMENT_COMPLETE, order.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(eventPublisher, times(1)).publishEvent(any(PaymentSuccessEvent.class));
    }

    @Test
    @DisplayName("결제가 실패하면 주문 상태가 PENDING으로 유지되고 실패 이벤트가 발행된다")
    public void whenPaymentFails_thenOrderStatusRemainsPending() {
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentGateway.process(any(PaymentMethod.class), any(BigDecimal.class))).thenReturn(false);

        paymentService.processPayment(new PaymentRequestEvent(this, order.getId(), paymentAmount.value(), PaymentMethod.CREDIT_CARD));

        assertEquals(OrderStatus.PENDING, order.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(eventPublisher, times(1)).publishEvent(any(PaymentFailedEvent.class));
    }

    @Test
    @DisplayName("결제 금액이 0이거나 음수일 경우 결제가 실패해야 한다")
    public void whenPaymentAmountIsZeroOrNegative_thenPaymentFails() {
        assertThrows(InvalidPaymentAmountException.class, () -> new PaymentAmount(BigDecimal.ZERO));
        assertThrows(InvalidPaymentAmountException.class, () -> new PaymentAmount(BigDecimal.valueOf(-1000)));
    }

    @Test
    @DisplayName("이미 결제된 주문에 대해서는 결제 요청이 처리되지 않는다")
    public void whenOrderAlreadyPaid_thenPaymentIsNotProcessed() {
        ReflectionTestUtils.setField(order, "status", OrderStatus.PAYMENT_COMPLETE);
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));

        paymentService.processPayment(new PaymentRequestEvent(this, order.getId(), paymentAmount.value(), PaymentMethod.CREDIT_CARD));

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishEvent(any(PaymentSuccessEvent.class));
        verify(eventPublisher, never()).publishEvent(any(PaymentFailedEvent.class));
    }

    @Test
    @DisplayName("결제 요청 시 주문이 존재하지 않으면 예외가 발생한다")
    public void whenOrderDoesNotExist_thenThrowException() {
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                paymentService.processPayment(new PaymentRequestEvent(this, 999L, paymentAmount.value(), PaymentMethod.CREDIT_CARD))
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishEvent(any(PaymentSuccessEvent.class));
        verify(eventPublisher, never()).publishEvent(any(PaymentFailedEvent.class));
    }

    @Test
    @DisplayName("다양한 결제 수단에 대해 결제가 성공적으로 처리된다")
    public void whenPaymentMethodIsDifferent_thenProcessPaymentSuccessfully() {
        PaymentMethod bankTransfer = PaymentMethod.BANK_TRANSFER;
        when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentGateway.process(eq(bankTransfer), any(BigDecimal.class))).thenReturn(true);

        paymentService.processPayment(new PaymentRequestEvent(this, order.getId(), paymentAmount.value(), bankTransfer));

        assertEquals(OrderStatus.PAYMENT_COMPLETE, order.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(eventPublisher, times(1)).publishEvent(any(PaymentSuccessEvent.class));
    }
}
