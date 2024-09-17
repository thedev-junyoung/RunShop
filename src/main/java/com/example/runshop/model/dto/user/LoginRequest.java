package com.example.runshop.model.dto.user;

import com.example.runshop.model.vo.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    private Email email;
    private String password;
}
