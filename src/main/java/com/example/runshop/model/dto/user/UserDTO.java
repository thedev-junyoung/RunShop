package com.example.runshop.model.dto.user;

import com.example.runshop.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private UserRole role;
    private String createdAt;
    private boolean enabled;
    private String updatedAt;


}
