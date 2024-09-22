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
import com.example.runshop.module.payment.domain.PaymentMethod;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.domain.Payment;
import com.example.runshop.module.payment.intrastructure.gateway.external.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentService implements ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;  // 인터페이스를 사용하여 결제 게이트웨이 주입
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentGateway paymentGateway,
                          ApplicationEventPublisher eventPublisher, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.eventPublisher = eventPublisher;
        this.orderRepository = orderRepository;
    }

    @Override
    public void processPayment(PaymentRequestEvent event) {
        // 주문을 조회하고 없으면 예외 발생
        Order order = findOrderById(event.getOrderId());
        // 주문이 이미 결제 완료 상태인 경우, 결제 처리를 하지 않고 리턴
        if (order.getStatus() == OrderStatus.PAYMENT_COMPLETE) {
            log.warn("이미 결제된 주문에 대해 결제 요청이 처리되지 않았습니다. 주문 ID: {}", event.getOrderId());
            return;
        }
        // Payment 객체 생성
        Payment payment = new Payment(order, new PaymentAmount(event.getAmount()));

        // 결제 수단을 이벤트에서 받아와 처리
        PaymentMethod method = event.getPaymentMethod();

        // 외부 결제 게이트웨이를 통해 결제 처리 (결제 성공/실패 여부 반환)
        boolean success = paymentGateway.process(method, event.getAmount());

        // 결제 성공 시
        if (success) {
            payment.markSuccess();  // 결제 상태를 SUCCESS로 변경

            // 주문 상태도 PAYMENT_COMPLETE로 변경
            order.setStatus(OrderStatus.PAYMENT_COMPLETE);
            orderRepository.save(order);  // 주문 상태 변경 후 저장

            // 결제 성공 이벤트 발행
            eventPublisher.publishEvent(new PaymentSuccessEvent(this, event.getOrderId()));
            log.info("결제가 성공적으로 처리되었습니다. 주문 ID: {}, 금액: {}", event.getOrderId(), event.getAmount());
        }
        // 결제 실패 시
        else {
            payment.markFailure();  // 결제 상태를 FAILURE로 변경

            // 결제 실패 이벤트 발행
            eventPublisher.publishEvent(new PaymentFailedEvent(this, event.getOrderId()));
            log.warn("결제가 실패하였습니다. 주문 ID: {}", event.getOrderId());
        }
        paymentRepository.save(payment);  // 상태 변경 후 저장
    }


    // 주문 ID로 주문을 조회하는 메서드
    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }


}
