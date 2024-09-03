package com.example.runshop.model.dto.user;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String createdAt;
    private boolean enabled;
    private String updatedAt;


}
