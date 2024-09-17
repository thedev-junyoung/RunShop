package com.example.runshop.model.vo.product;

import com.example.runshop.exception.product.CharactersArrangeDescriptionException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ProductDescription {

    @Column(name = "description")
    private String value;

    public ProductDescription(String value) {
        if (value == null || value.length() > 1000) {
            throw new CharactersArrangeDescriptionException("Description cannot exceed 1000 characters");

        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
