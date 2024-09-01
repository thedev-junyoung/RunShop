package com.example.shoppingmall.service;

import com.example.shoppingmall.model.dto.user.UpdatePasswordRequest;
import com.example.shoppingmall.model.dto.user.UpdateUserRequest;
import com.example.shoppingmall.repository.UserRepository;
import com.example.shoppingmall.utils.JWT;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.example.shoppingmall.model.entity.User;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, JWT jwt) {
        this.userRepository = userRepository;
    }



    public void updateUserDetails(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 사용자 정보 업데이트
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
/*
*  JPA는 엔티티를 수정할 때, 해당 엔티티의 모든 필드를 업데이트함
*  만약 일부 필드만 업데이트하고 싶다면, 해당 필드만 수정하고(set) save() 메서드를 호출하면 됨
* */
        userRepository.save(user);
        log.info("유저 상세 정보 수정 완료 userId: {}", userId);
    }

    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!checkPassword(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(hashPassword(request.getNewPassword()));
        userRepository.save(user);
        log.info("유저 패스워드 변경 완료 userId: {}", userId);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(false); // 계정 비활성화
        userRepository.save(user);
        log.info("유저 계정 비활성화 완료 userId: {}", userId);
    }
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    private boolean checkPassword(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }

}
