package com.example.runshop.service;

import com.example.runshop.model.dto.user.SignUpRequest;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;


@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(); // 모킹하지 않고 실제 인코더 사용

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void SuccessUpdatedUserInfo() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated User");
        updateUserRequest.setPhone("0987654321");
        updateUserRequest.setAddress(new Address("Updated Street", "Apt 202", "Updated City", "Updated Region", "12345"));

        // createUser 메서드로 User 생성
        User user = User.createUser(SignUpRequest.builder()
                        .email("test@example.com")
                        .password("encryptedPassword")  // 인코딩된 비밀번호를 전달
                        .name("Test User")
                        .phone("1234567890")
                        .address(new Address("Test Street", "Apt 101", "Test City", "Test Region", "54321"))
                        .build(),
                bCryptPasswordEncoder);

        // User 객체의 ID 필드를 수동으로 설정
        ReflectionTestUtils.setField(user, "id", 1L);

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        log.info("유저 수정 시작: {}", user.getId());
        userService.updateUserDetails(user.getId(), updateUserRequest);

        // 검증
        assertEquals("Updated User", user.getName());
        assertEquals("0987654321", user.getPhone());

        Address updatedAddress = updateUserRequest.getAddress();
        assertEquals(updatedAddress.street(), user.getAddress().street());
        assertEquals(updatedAddress.detailedAddress(), user.getAddress().detailedAddress());
        assertEquals(updatedAddress.city(), user.getAddress().city());
        assertEquals(updatedAddress.region(), user.getAddress().region());
        assertEquals(updatedAddress.zipCode(), user.getAddress().zipCode());

        log.info("유저 수정 성공: {}", user.getId());
    }
    @Test
    @DisplayName("비밀번호 변경 성공")
    void SuccessUpdatePassword() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("password123", "newPassword123");
        String oldHashedPassword = bCryptPasswordEncoder.encode("password123");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "password", new Password(oldHashedPassword));
        ReflectionTestUtils.setField(user, "email", new Email("test@example.com"));

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        log.info("userId에 대한 암호 업데이트 프로세스를 시작하는 중: {}", user.getId());
        userService.updatePassword(user.getId(), updatePasswordRequest);

        String newEncodedPassword = user.getPassword().value();

        assertNotEquals(oldHashedPassword, newEncodedPassword);
        assertTrue(bCryptPasswordEncoder.matches("newPassword123", newEncodedPassword));

        log.info("userId에 대한 암호 업데이트 테스트 통과: {}", user.getId());
    }
    @Test
    @DisplayName("유저 계정 비활성화 성공")
    void SuccessDisabledUser() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "enabled", true);

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        log.info("Starting user deactivation process for userId: {}", user.getId());
        userService.disabled(user.getId());

        assertFalse(user.isEnabled());
        log.info("User deactivation test passed for userId: {}", user.getId());
    }
}