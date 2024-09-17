package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.PriceNegativeException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Embeddable
@NoArgsConstructor
public class ProductPrice {

    @Column(name = "price", nullable = false)
    private BigDecimal value;

    public ProductPrice(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new PriceNegativeException("Price cannot be negative");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
