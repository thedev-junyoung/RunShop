package com.example.runshop.service;

import com.example.runshop.model.dto.user.*;

import java.util.List;

public interface UserService {
    void signUp(SignUpRequest request);
    UserDTO getUserById(Long userId);
    List<UserDTO> getAllUsers();
    void updateUserDetails(Long userId, UpdateUserRequest request);
    void updatePassword(Long userId, UpdatePasswordRequest request);
    void updateUserRole(Long userId, UpdateRoleRequest request);
    void disabled(Long userId);
}
