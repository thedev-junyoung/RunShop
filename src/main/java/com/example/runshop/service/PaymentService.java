package com.example.runshop.service;

import com.example.runshop.gateway.PaymentGateway;
import com.example.runshop.model.dto.payment.OrderRequest;
import com.example.runshop.model.entity.Order;
import com.example.runshop.model.entity.Payment;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.model.enums.PaymentStatus;
import com.example.runshop.repository.OrderRepository;
import com.example.runshop.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class PaymentService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public PaymentService(OrderService orderService, OrderRepository orderRepository, PaymentRepository paymentRepository, PaymentGateway paymentGateway) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public void processPayment(Long orderId, OrderRequest orderRequest) {
        Order order = orderService.findOrderOrThrow(orderId);

        // 주문 상태 확인
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("주문이 결제 가능한 상태가 아닙니다.");
        }

        // 결제 엔티티 생성 및 설정
        Payment payment = new Payment();
        payment.setMethod(orderRequest.getPaymentMethod());
        payment.setOrder(order);
        payment.setAmount(orderRequest.getAmount());

        // 결제 처리
        boolean paymentSuccess = paymentGateway.processPayment(orderRequest.getPaymentMethod(), orderRequest.getAmount());

        // 결제 성공 처리
        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.PAYMENT_SUCCESS);
            order.setStatus(OrderStatus.PAYMENT_COMPLETE);
            log.info("결제가 성공적으로 처리되었습니다. 주문 ID: {}", orderId);
        }
        // 결제 실패 처리
        else {
            payment.setStatus(PaymentStatus.PAYMENT_FAIL);
            log.warn("결제가 실패했습니다. 주문 ID: {}", orderId);
        }

        // 결제 정보 및 주문 정보 저장
        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}
