package com.example.runshop.model.dto.user;

import com.example.runshop.model.vo.user.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @Builder @NoArgsConstructor
public class SignUpRequest {
    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Name is required")
    private String name;

    private String phone;

    // Address 필드를 하나의 객체로 통합
    private Address address;

}
