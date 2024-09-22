package com.example.runshop.module.payment.application.port.out;

import com.example.runshop.module.payment.domain.Payment;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
}
