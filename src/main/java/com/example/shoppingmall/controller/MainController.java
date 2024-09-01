package com.example.shoppingmall.controller;

import com.example.shoppingmall.model.dto.user.SignUpRequest;
import com.example.shoppingmall.model.dto.user.LoginRequest;
import com.example.shoppingmall.service.SignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {
    private final SignUpService signUpService;

    public MainController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @PostMapping("/join")
    public ResponseEntity<Object> join(@RequestBody SignUpRequest request){
        signUpService.signUp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request){
        signUpService.login(request);
        return ResponseEntity.ok().build();
    }
}
