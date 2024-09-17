package com.example.runshop.model.vo;

import com.example.runshop.exception.user.InvalidEmailException;
import com.fasterxml.jackson.annotation.JsonValue; // 추가
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Email {

    @NotEmpty
    @jakarta.validation.constraints.Email
    @Column(name = "email")
    private String emailValue;

    // 생성자
    public Email(String value) {
        if (!isValidEmail(value)) {
            throw new InvalidEmailException("유효하지 않은 이메일 형식입니다.");
        }
        this.emailValue = value;
    }

    // 이메일 유효성 검사 메서드
    private boolean isValidEmail(String value) {
        return value != null && value.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    @Override
    @JsonValue // JSON 직렬화 시 이 메서드로 변환
    public String toString() {
        return emailValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return emailValue.equals(email.emailValue);
    }

    @Override
    public int hashCode() {
        return emailValue.hashCode();
    }
}
