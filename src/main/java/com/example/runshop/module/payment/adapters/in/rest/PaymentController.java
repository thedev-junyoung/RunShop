package com.example.runshop.module.payment.adapters.in.rest;

import com.example.runshop.model.vo.payment.PaymentAmount;
import com.example.runshop.module.payment.adapters.in.event.PaymentRequestEvent;
import com.example.runshop.module.payment.domain.PaymentMethod;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final ApplicationEventPublisher eventPublisher;

    public PaymentController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(
            @RequestParam Long orderId,
            @RequestParam BigDecimal amount,
            @RequestParam PaymentMethod method) {

        PaymentRequestEvent event = new PaymentRequestEvent(this, orderId, new PaymentAmount(amount).value(), method);
        eventPublisher.publishEvent(event);

        return ResponseEntity.accepted().body("Payment request has been accepted and is being processed");
    }
}