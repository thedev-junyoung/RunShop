package com.example.runshop.model.dto.user;

import com.example.runshop.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateRoleRequest {
    private UserRole role;
}
