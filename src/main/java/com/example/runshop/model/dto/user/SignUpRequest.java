package com.example.runshop.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor @Builder
public class SignUpRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;


}
