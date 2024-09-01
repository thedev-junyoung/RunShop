package com.example.shoppingmall.service;

import com.example.shoppingmall.model.dto.user.LoginRequest;
import com.example.shoppingmall.model.dto.user.SignUpRequest;
import com.example.shoppingmall.model.entity.User;
import com.example.shoppingmall.model.enums.UserRole;
import com.example.shoppingmall.repository.UserRepository;
import com.example.shoppingmall.utils.JWT;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignUpService {
    private final UserRepository userRepository;
    private final JWT jwt;

    public SignUpService(UserRepository userRepository, JWT jwt) {
        this.userRepository = userRepository;
        this.jwt = jwt;
    }

    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("중복된 이메일 입니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashPassword(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(UserRole.USER);

        userRepository.save(user);
        log.info("유저 회원가입 성공 : {}", user.getEmail());
    }
    public String login(LoginRequest request) {
        log.info("유저 로그인 email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !checkPassword(request.getPassword(), user.getPassword())) {
            log.warn("Login 실패 for email: {}", request.getEmail());
            // 고정 시간 비교를 위해 더미 비밀번호 체크 수행
            checkPassword(request.getPassword(), "$2a$10$dummyHashForTimingAttacks");
            return null; // 로그인 실패 시 null 반환
        }

        log.info("유저 로그인 성공 email: {}", user.getEmail());
        return jwt.createJwt(user.getEmail(), user.getRole().name(), 3600000L); // 1시간 유효한 토큰 생성
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    private boolean checkPassword(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }
}
