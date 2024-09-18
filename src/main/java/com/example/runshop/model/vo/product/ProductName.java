package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.CharactersArrangeException;
import com.example.runshop.utils.customaonotation.ProductNameValid;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProductName(@ProductNameValid String value) {
    @JsonCreator
    public ProductName {
        if (value == null || value.length() < 3 || value.length() > 255) {
            throw new CharactersArrangeException("제품 이름은 3~255자 사이여야 합니다.");
        }
    }
    @JsonValue
    public String value() {
        return value;
    }
}
