package com.example.shoppingmall.controller;

import com.example.shoppingmall.model.dto.user.SignUpRequest;
import com.example.shoppingmall.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입
    @RequestMapping("/signup")
    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
        userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created 상태 코드 반환
    }
}
