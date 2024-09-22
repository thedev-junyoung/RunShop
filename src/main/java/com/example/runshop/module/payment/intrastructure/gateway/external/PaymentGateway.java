package com.example.runshop.module.payment.intrastructure.gateway.external;


import com.example.runshop.module.payment.domain.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentGateway {
    boolean process(PaymentMethod paymentMethod, BigDecimal amount);
}
