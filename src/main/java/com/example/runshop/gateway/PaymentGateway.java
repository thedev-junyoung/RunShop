package com.example.runshop.gateway;

import com.example.runshop.model.enums.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentGateway {
    boolean processPayment(PaymentMethod paymentMethod, BigDecimal amount);
}
