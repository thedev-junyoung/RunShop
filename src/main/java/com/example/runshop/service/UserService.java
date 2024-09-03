package com.example.runshop.service;

import com.example.runshop.model.dto.user.SignUpRequest;
import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.repository.UserRepository;
import com.example.runshop.utils.JWT;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.runshop.model.entity.User;

import java.util.List;

@Service // 이 클래스가 Spring의 서비스 컴포넌트임을 나타냄
@Slf4j // 로그 출력을 위한 Lombok 애노테이션
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 생성자를 통한 의존성 주입
    public UserService(UserRepository userRepository, JWT jwt, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 회원가입 처리
    public void signUp(SignUpRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("중복된 이메일 입니다.");
        }

        // 새로운 User 엔티티 생성 및 필드 설정
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword())); // 비밀번호 암호화
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        // User 엔티티 저장
        userRepository.save(user);
        log.info("유저 회원가입 성공 : {}", user.getEmail());
    }

    // 특정 유저 조회
    public User getUserById(Long userId) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 전체 유저 조회
    public List<User> getAllUsers() {
        // 모든 유저를 조회하여 반환
        return userRepository.findAll();
    }

    // 유저 정보 업데이트
    public void updateUserDetails(Long userId, UpdateUserRequest request) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 사용자 정보 업데이트
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        /*
         * JPA는 엔티티를 수정할 때, 해당 엔티티의 모든 필드를 업데이트함
         * 만약 일부 필드만 업데이트하고 싶다면, 해당 필드만 수정하고(set) save() 메서드를 호출하면 됨
         */

        // 변경된 User 엔티티 저장
        userRepository.save(user);
        log.info("유저 상세 정보 수정 완료 userId: {}", userId);
    }

    // 패스워드 업데이트
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 기존 패스워드가 일치하는지 확인
        if (!checkPassword(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // 새로운 비밀번호 설정 및 암호화
        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));

        // 변경된 User 엔티티 저장
        userRepository.save(user);
        log.info("유저 패스워드 변경 완료 userId: {}", userId);
    }

    // 유저 계정 비활성화
    public void deactivateUser(Long userId) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 계정 비활성화 (enabled를 false로 설정)
        user.setEnabled(false);

        // 변경된 User 엔티티 저장
        userRepository.save(user);
        log.info("유저 계정 비활성화 완료 userId: {}", userId);
    }

    // 비밀번호 일치 여부 확인
    private boolean checkPassword(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed); // 주어진 평문 비밀번호와 저장된 해시된 비밀번호를 비교
    }

}
