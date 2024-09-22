package com.example.runshop.module.payment.adapters.in.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentFailedEvent extends ApplicationEvent {
    private final Long orderId;

    public PaymentFailedEvent(Object source, Long orderId) {
        super(source);  // ApplicationEvent의 생성자 호출
        this.orderId = orderId;
    }

}

