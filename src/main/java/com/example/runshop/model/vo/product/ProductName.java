package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.CharactersArrangeException;
import com.example.runshop.utils.customaonotation.ProductNameValid;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class ProductName {

    @Column(name = "name", nullable = false)
    @ProductNameValid
    private String value;

    @JsonCreator
    public ProductName(String value) {
        if (value == null || value.length() < 3 || value.length() > 255) {
            throw new CharactersArrangeException("Name must be between 3 and 255 characters");

        }
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
