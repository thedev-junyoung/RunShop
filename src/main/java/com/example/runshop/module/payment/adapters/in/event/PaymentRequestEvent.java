package com.example.runshop.module.payment.adapters.in.event;

import com.example.runshop.module.payment.domain.PaymentMethod;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class PaymentRequestEvent extends ApplicationEvent {
    private final Long orderId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;  // 결제 수단 추가

    public PaymentRequestEvent(Object source, Long orderId, BigDecimal amount, PaymentMethod paymentMethod) {
        super(source);
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

}