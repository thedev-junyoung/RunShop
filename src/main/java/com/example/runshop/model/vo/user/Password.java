package com.example.runshop.model.vo.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Slf4j
public record Password(String value) {
    @JsonCreator
    public Password {
        if (value == null || value.length() < 8) {
            log.info(String.valueOf(value));
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }

    @Override
    @JsonValue
    public String value() {
        return value;
    }

    // 기존 비밀번호와 새로운 비밀번호를 비교하는 메서드
    public boolean matches(BCryptPasswordEncoder encoder, String rawPassword) {
        return encoder.matches(rawPassword, this.value);
    }
}

