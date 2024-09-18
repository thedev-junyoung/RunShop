package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.PriceNegativeException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor
public class ProductPrice {

    @Column(name = "price", nullable = false)
    private BigDecimal value;
    @JsonCreator
    public ProductPrice(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new PriceNegativeException("Price cannot be negative");
        }
        this.value = value;
    }
    @JsonValue
    public BigDecimal getValue() {
        return value;
    }
    @Override
    public String toString() {
        return value.toString();
    }
}
