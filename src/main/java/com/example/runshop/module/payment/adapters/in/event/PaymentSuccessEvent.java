package com.example.runshop.module.payment.adapters.in.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentSuccessEvent extends ApplicationEvent {
    private final Long orderId;

    public PaymentSuccessEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }

}
