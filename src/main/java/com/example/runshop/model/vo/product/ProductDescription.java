package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.CharactersArrangeDescriptionException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProductDescription(String value) {
    @JsonCreator
    public ProductDescription {
        if (value == null || value.length() > 1000) {
            throw new CharactersArrangeDescriptionException("Description cannot exceed 1000 characters");
        }
    }

    @JsonValue
    public String value() {
        return value;
    }
}
