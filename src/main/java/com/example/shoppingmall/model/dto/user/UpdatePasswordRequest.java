package com.example.shoppingmall.model.dto.user;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    String oldPassword;
    String newPassword;
    public UpdatePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }


}
