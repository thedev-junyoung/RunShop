package com.example.runshop.gateway;

import com.example.runshop.model.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExternalPaymentGateway implements PaymentGateway {

    @Override
    public boolean processPayment(PaymentMethod paymentMethod, BigDecimal amount) {
        // 외부 결제 API 호출 로직 구현
        // 여기서는 무조건 성공한다고 가정
        return true;
    }
}
