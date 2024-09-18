package com.example.runshop.model.vo.payment;

import com.example.runshop.exception.payment.InvalidPaymentAmountException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record PaymentAmount(BigDecimal value) {
    @JsonCreator
    public PaymentAmount {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentAmountException("결제 금액은 0보다 커야 합니다.");
        }
    }

    @JsonValue
    public BigDecimal value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
