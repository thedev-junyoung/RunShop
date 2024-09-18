package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.PriceNegativeException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record ProductPrice(BigDecimal value) {

    @JsonCreator
    public ProductPrice {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new PriceNegativeException("Price cannot be negative");
        }
    }

    @JsonValue
    public BigDecimal value() {
        return value;
    }
}
