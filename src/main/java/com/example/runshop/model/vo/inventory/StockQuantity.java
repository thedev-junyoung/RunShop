package com.example.runshop.model.vo.inventory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;

@Embeddable
public record StockQuantity(int value) {
    @JsonCreator
    public StockQuantity {
        if (value < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }
    }

    @JsonValue
    public int value() {
        return value;
    }

    // 재고 증가 메서드
    public StockQuantity increaseStock(int quantity) {
        return new StockQuantity(this.value + quantity);
    }

    // 재고 감소 메서드
    public StockQuantity decreaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("감소할 수량은 음수일 수 없습니다.");
        }

        if (this.value < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        return new StockQuantity(this.value - quantity);
    }
}
