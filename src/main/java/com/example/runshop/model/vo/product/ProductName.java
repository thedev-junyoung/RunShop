package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.CharactersArrangeException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ProductName {

    @Column(name = "name", nullable = false)
    private String value;

    public ProductName(String value) {
        if (value == null || value.length() < 3 || value.length() > 255) {
            throw new CharactersArrangeException("Name must be between 3 and 255 characters");

        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
