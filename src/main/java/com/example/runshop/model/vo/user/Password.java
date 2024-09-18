package com.example.runshop.model.vo.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Password(String value) {
    @JsonCreator
    public Password {
        if (value == null || value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }

    @Override
    @JsonValue
    public String value() {
        return value;
    }
}

