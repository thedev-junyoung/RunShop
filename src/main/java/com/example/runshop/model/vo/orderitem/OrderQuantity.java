package com.example.runshop.model.vo.orderitem;

import com.example.runshop.exception.orderitem.QuantityNegativeException;
import jakarta.persistence.Embeddable;

@Embeddable
public record OrderQuantity(int value) {
    public OrderQuantity {
        if (value <= 0) {
            throw new QuantityNegativeException("수량은 0보다 커야 합니다.");
        }
    }
}
