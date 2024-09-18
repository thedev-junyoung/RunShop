package com.example.runshop.model.vo.payment;

import com.example.runshop.exception.payment.InvalidPaymentAmountException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor
public class PaymentAmount {
    private BigDecimal value;
    @JsonCreator // JSON으로부터 객체 생성
    public PaymentAmount(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentAmountException("결제 금액은 0보다 커야 합니다.");
        }
        this.value = value;
    }
    @JsonValue // 객체를 JSON으로 변환
    public BigDecimal getValue() {
        return value;
    }
    @Override
    public String toString() {
        return value.toString();
    }
}
