package com.example.shoppingmall;


import com.example.shoppingmall.model.dto.user.LoginRequest;
import com.example.shoppingmall.model.dto.user.SignUpRequest;
import com.example.shoppingmall.model.dto.user.UpdatePasswordRequest;
import com.example.shoppingmall.model.dto.user.UpdateUserRequest;
import com.example.shoppingmall.model.entity.User;
import com.example.shoppingmall.model.enums.UserRole;
import com.example.shoppingmall.repository.UserRepository;
import com.example.shoppingmall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void 회원가입_성공() {
        // Given
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "password123", "Test User", "1234567890", "Test Address");

        // 비밀번호를 해싱하여 저장할 사용자 객체 생성
        String hashedPassword = hashPassword(signUpRequest.getPassword());
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(hashedPassword); // 해싱된 비밀번호 사용
        user.setName(signUpRequest.getName());
        user.setPhone(signUpRequest.getPhone());
        user.setAddress(signUpRequest.getAddress());
        user.setRole(UserRole.USER);

        // Mocking
        Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When
        log.info("프로세스를 시작: {}", signUpRequest.getEmail());
        userService.signUp(signUpRequest);

        // Then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        log.info("사용자 가입 테스트 통과: {}", user.getEmail());
    }

    @Test
    void 로그인_성공() {
        // Given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        // 이미 저장된 사용자를 가정하고 테스트
        String hashedPassword = hashPassword(loginRequest.getPassword());
        User user = new User();
        user.setEmail(loginRequest.getEmail());
        user.setPassword(hashedPassword); // 해싱된 비밀번호 사용
        user.setRole(UserRole.USER);

        // Mocking
        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));

        // When
        log.info("로그인 프로세스 시작: {}", loginRequest.getEmail());
        String token = userService.login(loginRequest);

        // Then
        assertNotNull(token);
        assertEquals(UserRole.USER, user.getRole());
        log.info("로그인 프로세스 성공: {}", user.getEmail());
    }

    @Test
    void 사용자_정보_수정_성공() {
        // Given
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("Updated User", "0987654321", "Updated Address");
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setPhone("1234567890");
        user.setAddress("Test Address");
        user.setEmail("test@example.com");
        user.setRole(UserRole.USER);

        // Mocking
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When
        log.info("유저 수정 시작: {}", user.getId());
        userService.updateUserDetails(user.getId(), updateUserRequest);

        // Then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        assertEquals("Updated User", user.getName());
        assertEquals("0987654321", user.getPhone());
        assertEquals("Updated Address", user.getAddress());
        log.info("유저 수정 성공: {}", user.getId());
    }

    @Test
    void 비밀번호_변경_성공() {
        // Given
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("password123", "newPassword123");
        String oldHashedPassword = hashPassword("password123");
        User user = new User();
        user.setId(1L);
        user.setPassword(oldHashedPassword);
        user.setEmail("test@example.com");

        // Mocking
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When
        log.info("userId에 대한 암호 업데이트 프로세스를 시작하는 중: {}", user.getId());
        userService.updatePassword(user.getId(), updatePasswordRequest);

        // Then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        log.info("userId에 대한 암호 업데이트 테스트 통과: {}", user.getId());
    }


    @Test
    void 유저_계정_비활성화_성공() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEnabled(true); // 초기값은 활성화 상태

        // Mocking
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When
        log.info("Starting user deactivation process for userId: {}", user.getId());
        userService.deactivateUser(user.getId());

        // Then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        assertEquals(false, user.isEnabled()); // 계정이 비활성화되었는지 확인
        log.info("User deactivation test passed for userId: {}", user.getId());
    }
    // 테스트 클래스에서 유틸리티 메서드를 활용할 수 있도록 추가
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}