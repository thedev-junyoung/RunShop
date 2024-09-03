package com.example.runshop.service;

import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.model.entity.User;
import com.example.runshop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
    private UserService userService; // 테스트 대상 서비스

    @MockBean
    private UserRepository userRepository; // UserRepository의 모의 객체 생성

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder; // BCryptPasswordEncoder의 모의 객체 생성

    @Test
    void 사용자_정보_수정_성공() {
        // Given: 테스트 데이터 및 환경 설정
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("Updated User", "0987654321", "Updated Address");
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setPhone("1234567890");
        user.setAddress("Test Address");
        user.setEmail("test@example.com");

        // Mocking: UserRepository의 동작 정의
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When: 테스트 대상 메서드 실행
        log.info("유저 수정 시작: {}", user.getId());
        userService.updateUserDetails(user.getId(), updateUserRequest);

        // Then: 테스트 결과 검증
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class)); // save 메서드가 한 번 호출되었는지 확인
        assertEquals("Updated User", user.getName());
        assertEquals("0987654321", user.getPhone());
        assertEquals("Updated Address", user.getAddress());
        log.info("유저 수정 성공: {}", user.getId());
    }

    @Test
    void 비밀번호_변경_성공() {
        // Given: 테스트 데이터 및 환경 설정
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("password123", "newPassword123");
        String oldHashedPassword = hashPassword("password123");
        User user = new User();
        user.setId(1L);
        user.setPassword(oldHashedPassword);
        user.setEmail("test@example.com");

        // Mocking: UserRepository와 BCryptPasswordEncoder의 동작 정의
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(bCryptPasswordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.encode(Mockito.anyString())).thenReturn("newHashedPassword");

        // When: 테스트 대상 메서드 실행
        log.info("userId에 대한 암호 업데이트 프로세스를 시작하는 중: {}", user.getId());
        userService.updatePassword(user.getId(), updatePasswordRequest);

        // Then: 테스트 결과 검증
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class)); // save 메서드가 한 번 호출되었는지 확인
        Mockito.verify(bCryptPasswordEncoder, Mockito.times(1)).encode(Mockito.anyString()); // encode 메서드가 한 번 호출되었는지 확인
        assertNotEquals(oldHashedPassword, user.getPassword()); // 비밀번호가 변경되었는지 확인
        log.info("userId에 대한 암호 업데이트 테스트 통과: {}", user.getId());
    }

    @Test
    void 유저_계정_비활성화_성공() {
        // Given: 테스트 데이터 및 환경 설정
        User user = new User();
        user.setId(1L);
        user.setEnabled(true); // 초기값은 활성화 상태

        // Mocking: UserRepository의 동작 정의
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When: 테스트 대상 메서드 실행
        log.info("Starting user deactivation process for userId: {}", user.getId());
        userService.deactivateUser(user.getId());

        // Then: 테스트 결과 검증
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class)); // save 메서드가 한 번 호출되었는지 확인
        assertFalse(user.isEnabled()); // 계정이 비활성화되었는지 확인
        log.info("User deactivation test passed for userId: {}", user.getId());
    }

    // 테스트 클래스에서 유틸리티 메서드를 활용할 수 있도록 추가
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}