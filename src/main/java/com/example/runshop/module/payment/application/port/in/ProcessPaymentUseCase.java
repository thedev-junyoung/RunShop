package com.example.runshop.module.payment.application.port.in;

import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

public interface ProcessPaymentUseCase {
    // 결제 요청 이벤트 처리 메서드
    @EventListener
    @Transactional
    void processPayment(PaymentRequestEvent event);
}