package com.example.runshop.module.payment.intrastructure.gateway.external;

import com.example.runshop.module.payment.domain.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExternalPaymentGateway implements PaymentGateway {

    @Override
    public boolean process(PaymentMethod paymentMethod, BigDecimal amount) {
        // 외부 결제 API 호출 로직 (예: Toss, Stripe 등)
        // 여기서는 테스트 목적으로 결제를 항상 성공 처리
        System.out.println("Processing payment via external gateway...");
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Amount: " + amount);

        // 실제로 외부 API와 통신을 할 수 있는 코드가 들어가야 합니다.
        // 여기서는 무조건 결제가 성공한다고 가정
        return true;
    }
}