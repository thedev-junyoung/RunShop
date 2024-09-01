package com.example.shoppingmall;

import com.example.shoppingmall.model.dto.user.LoginRequest;
import com.example.shoppingmall.model.dto.user.SignUpRequest;
import com.example.shoppingmall.model.dto.user.UpdatePasswordRequest;
import com.example.shoppingmall.model.dto.user.UpdateUserRequest;
import com.example.shoppingmall.model.entity.User;
import com.example.shoppingmall.model.enums.UserRole;
import com.example.shoppingmall.repository.UserRepository;
import com.example.shoppingmall.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional // 테스트 종료 후 롤백
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 회원가입_및_로그인_성공() {

        // 1. 회원가입 테스트
        // Given
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "password123", "Test User", "1234567890", "Test Address");

        // When
        userService.signUp(signUpRequest);

        // Then
        User savedUser = userRepository.findByEmail("test@example.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test User", savedUser.getName());

        // 2. 로그인 테스트
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        // When
        String token = userService.login(loginRequest);

        // Then
        assertNotNull(token);
        assertEquals(UserRole.USER, savedUser.getRole());
    }

    @Test
    void 사용자_정보_수정_성공() {
        // 1. 회원가입 먼저 실행
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "password123", "Test User", "1234567890", "Test Address");
        userService.signUp(signUpRequest);

        // 2. 사용자 정보 수정 테스트
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("Updated User", "0987654321", "Updated Address");
        User savedUser = userRepository.findByEmail("test@example.com").orElse(null);

        assertNotNull(savedUser); // 사용자 확인
        userService.updateUserDetails(savedUser.getId(), updateUserRequest);

        // 3. 수정된 사용자 정보 확인
        User updatedUser = userRepository.findByEmail("test@example.com").orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getName());
        assertEquals("0987654321", updatedUser.getPhone());
        assertEquals("Updated Address", updatedUser.getAddress());
    }

    @Test
    void 비밀번호_변경_성공() {
        // 1. 회원가입 먼저 실행
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "password123", "Test User", "1234567890", "Test Address");
        userService.signUp(signUpRequest);

        // 2. 비밀번호 변경 테스트
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("password123", "newPassword123");
        User savedUser = userRepository.findByEmail("test@example.com").orElse(null);

        assertNotNull(savedUser); // 사용자 확인
        userService.updatePassword(savedUser.getId(), updatePasswordRequest);

        // 3. 로그인 시 새로운 비밀번호 사용 가능 확인
        LoginRequest loginRequest = new LoginRequest("test@example.com", "newPassword123");
        String token = userService.login(loginRequest);

        assertNotNull(token);
    }

    @Test
    void 유저_계정_비활성화_성공() {
        // 1. 회원가입 먼저 실행
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "password123", "Test User", "1234567890", "Test Address");
        userService.signUp(signUpRequest);

        // 2. 계정 비활성화 테스트
        User savedUser = userRepository.findByEmail("test@example.com").orElse(null);

        assertNotNull(savedUser); // 사용자 확인
        userService.deactivateUser(savedUser.getId());

        // 3. 계정 비활성화 확인
        User deactivatedUser = userRepository.findByEmail("test@example.com").orElse(null);
        assertNotNull(deactivatedUser);
        assertEquals(false, deactivatedUser.isEnabled());
    }

}
