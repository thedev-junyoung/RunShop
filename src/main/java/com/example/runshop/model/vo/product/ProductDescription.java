package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.CharactersArrangeDescriptionException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class ProductDescription {

    @Column(name = "description")
    private String value;
    @JsonCreator
    public ProductDescription(String value) {
        if (value == null || value.length() > 1000) {
            throw new CharactersArrangeDescriptionException("Description cannot exceed 1000 characters");

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
