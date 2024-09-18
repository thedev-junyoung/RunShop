package com.example.runshop.model.vo.inventory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class StockQuantity {
    @Column(name = "stock_quantity", nullable = false)
    private int value;

    @JsonCreator
    public StockQuantity(int quantity) {
        this.value = quantity;
    }
    @JsonValue
    public int getValue() {
        return value;
    }

    // 재고 증가 메서드
    public void increaseStock(int quantity) {
        this.value += quantity;
    }

    // 재고 감소 메서드
    public void decreaseStock(int quantity) {
        if (this.value < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.value -= quantity;
    }
}
