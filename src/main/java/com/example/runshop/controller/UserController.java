package com.example.runshop.controller;

import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateRoleRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#id).email == authentication.principal.username")
    public ResponseEntity<?> getUser(@PathVariable Long id, HttpServletRequest request) {
        UserDTO user = userService.getUserById(id);
        return SuccessResponse.ok("사용자 정보를 성공적으로 조회했습니다.", user, request.getRequestURI());
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Pageable pageable, HttpServletRequest request) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return SuccessResponse.ok("모든 사용자 정보를 성공적으로 조회했습니다.", users, request.getRequestURI());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#id).email == authentication.principal.username")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request, HttpServletRequest httpRequest) {
        userService.updateUserDetails(id, request);
        return SuccessResponse.ok("사용자 정보가 성공적으로 업데이트되었습니다.", httpRequest.getRequestURI());
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("@userService.getUserById(#id).email == authentication.principal.username")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest request, HttpServletRequest httpRequest) {
        userService.updatePassword(id, request);
        return SuccessResponse.ok("비밀번호가 성공적으로 변경되었습니다.", httpRequest.getRequestURI());
    }
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request, HttpServletRequest httpRequest) {
        userService.updateUserRole(id, request);
        return SuccessResponse.ok("사용자 권한이 성공적으로 변경되었습니다.", httpRequest.getRequestURI());
    }
    @DeleteMapping("/{id}/disabled")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#id).email == authentication.principal.username")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id, HttpServletRequest httpRequest) {
        userService.disabled(id);
        return SuccessResponse.ok("사용자 계정이 성공적으로 비활성화되었습니다.", httpRequest.getRequestURI());
    }
}