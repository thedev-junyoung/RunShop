package com.example.runshop.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

@Embeddable
@Getter
@NoArgsConstructor
public class Password {

    @Column(name = "password")
    private String passwordValue;

    public Password(String passwordValue) {
        this.passwordValue = passwordValue;
    }

    public boolean checkPassword(String rawPassword) {
        return BCrypt.checkpw(rawPassword, this.passwordValue);
    }
}
