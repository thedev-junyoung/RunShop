package com.example.runshop.controller;

import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.model.entity.User;
import com.example.runshop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // 특정 유저 조회
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user); // 200 OK 상태 코드 반환
    }

    // 전체 유저 조회
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users); // 200 OK 상태 코드 반환
    }

    // 유저 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        userService.updateUserDetails(id, request);
        return ResponseEntity.ok().build(); // 200 OK 상태 코드 반환
    }

    // 유저 비밀번호 수정
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok().build(); // 200 OK 상태 코드 반환
    }

    // 유저 삭제(비활성화)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content 상태 코드 반환
    }
}
