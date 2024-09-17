package com.example.runshop.model.vo.orderitem;

import com.example.runshop.exception.orderitem.QuantityNegativeException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class OrderQuantity {
    @Column(name = "quantity")
    private int value;

    public OrderQuantity(int value) {
        if (value <= 0) {
            throw new QuantityNegativeException("수량은 0보다 커야 합니다.");

        }
        this.value = value;
    }

}

