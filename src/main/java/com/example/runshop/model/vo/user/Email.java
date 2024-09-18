package com.example.runshop.model.vo.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Email(String value) {

    @JsonCreator
    public Email {
        if (value == null || !value.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    @JsonValue
    @Override
    public String value() {
        return value;
    }
}
