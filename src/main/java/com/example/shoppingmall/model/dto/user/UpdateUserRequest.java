package com.example.shoppingmall.model.dto.user;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String phone;
    private String address;

    public UpdateUserRequest(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
}
