// PaymentService.java
package com.example.runshop.module.payment.application.service;

import com.example.runshop.exception.order.OrderNotFoundException;
import com.example.runshop.model.enums.OrderStatus;
import com.example.runshop.model.vo.payment.PaymentAmount;
import com.example.runshop.module.order.domain.Order;
import com.example.runshop.module.order.application.port.out.OrderRepository;
import com.example.runshop.module.payment.adapters.in.event.PaymentFailedEvent;
import com.example.runshop.module.payment.adapters.in.event.PaymentSuccessEvent;
import com.example.runshop.module.payment.application.port.in.ProcessPaymentUseCase;
import com.example.runshop.module.payment.application.port.out.PaymentRepository;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.domain.Payment;
import com.example.runshop.module.payment.intrastructure.gateway.external.PaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService implements ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void processPayment(PaymentRequestEvent event) {
        Order order = findOrderById(event.getOrderId());
        if (order.getStatus() == OrderStatus.PAYMENT_COMPLETE) {
            log.warn("이미 결제된 주문입니다. 주문 ID: {}", event.getOrderId());
            return;
        }

        // 팩토리 메서드를 통해 Payment 객체 생성
        Payment payment = Payment.create(order, new PaymentAmount(event.getAmount()), event.getPaymentMethod());
        boolean success = paymentGateway.process(event.getPaymentMethod(), event.getAmount());

        payment.process(success);
        paymentRepository.save(payment);

        if (success) {
            eventPublisher.publishEvent(new PaymentSuccessEvent(this, event.getOrderId()));
            log.info("결제가 성공적으로 처리되었습니다. 주문 ID: {}", event.getOrderId());
        } else {
            eventPublisher.publishEvent(new PaymentFailedEvent(this, event.getOrderId()));
            log.warn("결제가 실패했습니다. 주문 ID: {}", event.getOrderId());
        }
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
