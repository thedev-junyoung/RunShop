package com.example.runshop.service;

import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.vo.user.Address;
import com.example.runshop.model.vo.user.Email;
import com.example.runshop.model.vo.user.Password;
import com.example.runshop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest // Spring Boot 애플리케이션 컨텍스트를 로드하여 통합 테스트 환경을 제공
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService; // 인터페이스를 통해 서비스 주입

    @MockBean
    private UserRepository userRepository; // UserRepository의 모의 객체 생성

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder; // BCryptPasswordEncoder의 모의 객체 생성

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void SuccessUpdatedUserInfo() {
        // Given
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated User");
        updateUserRequest.setPhone("0987654321");
        updateUserRequest.setAddress(new Address("Updated Street", "Apt 202", "Updated City", "Updated Region", "12345"));

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setPhone("1234567890");
        user.setAddress(new Address("Test Street", "Apt 101", "Test City", "Test Region", "54321"));
        user.setEmail(new Email("test@example.com"));

        // Mocking
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When
        log.info("유저 수정 시작: {}", user.getId());
        userService.updateUserDetails(user.getId(), updateUserRequest);

        // Then
        assertEquals("Updated User", user.getName());
        assertEquals("0987654321", user.getPhone());
        assertEquals("Updated Street", user.getAddress().getStreet());
        assertEquals("Apt 202", user.getAddress().getDetailedAddress());
        log.info("유저 수정 성공: {}", user.getId());
    }
    @Test
    @DisplayName("비밀번호 변경 성공")
    void SuccessUpdatePassword() {
        // Given
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("password123", "newPassword123");

        // 기존 비밀번호를 암호화한 값
        String oldHashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt());

        // User 엔티티 설정
        User user = new User();
        user.setId(1L);
        user.setPassword(new Password(oldHashedPassword)); // 기존 비밀번호 설정
        user.setEmail(new Email("test@example.com"));

        // Mocking: userRepository와 bCryptPasswordEncoder의 동작을 설정
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches("password123", oldHashedPassword)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");

        // When
        log.info("userId에 대한 암호 업데이트 프로세스를 시작하는 중: {}", user.getId());
        userService.updatePassword(user.getId(), updatePasswordRequest);

        // Then
        // 비밀번호 암호화가 제대로 되었는지 확인
        Mockito.verify(bCryptPasswordEncoder, Mockito.times(1)).encode("newPassword123");

        // 새로운 비밀번호가 저장되었는지 확인
        assertNotEquals(oldHashedPassword, user.getPassword().getPasswordValue());

        // 암호화된 새 비밀번호가 저장되었는지 확인
        assertEquals("newHashedPassword", user.getPassword().getPasswordValue());

        log.info("userId에 대한 암호 업데이트 테스트 통과: {}", user.getId());
    }

    @Test
    @DisplayName("유저 계정 비활성화 성공")
    void SuccessDisabledUser() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEnabled(true);

        // Mocking
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When
        log.info("Starting user deactivation process for userId: {}", user.getId());
        userService.disabled(user.getId());

        // Then
        assertFalse(user.isEnabled());
        log.info("User deactivation test passed for userId: {}", user.getId());

    }

}