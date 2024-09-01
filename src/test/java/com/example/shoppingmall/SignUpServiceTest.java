package com.example.shoppingmall;

import com.example.shoppingmall.model.dto.user.LoginRequest;
import com.example.shoppingmall.model.dto.user.SignUpRequest;
import com.example.shoppingmall.model.dto.user.UpdatePasswordRequest;
import com.example.shoppingmall.model.entity.User;
import com.example.shoppingmall.model.enums.UserRole;
import com.example.shoppingmall.repository.UserRepository;
import com.example.shoppingmall.service.SignUpService;
import com.example.shoppingmall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
@SpringBootTest
@Slf4j
public class SignUpServiceTest {
    @Autowired
    private SignUpService signUpService;
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

        // Mocking: 이미 존재하는 이메일인지 확인하는 메서드를 모킹하여 false 반환
        Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        // Mocking: 사용자를 저장하는 메서드를 모킹하여 user 객체 반환
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // When
        log.info("프로세스를 시작: {}", signUpRequest.getEmail());
        signUpService.signUp(signUpRequest);

        // Then: UserRepository의 save 메서드가 한 번 호출되었는지 확인
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        log.info("사용자 가입 테스트 통과: {}", user.getEmail());
    }


    @Test
    void 중복_이메일_회원가입_실패() {
        // Given: 이미 존재하는 이메일로 회원가입 시도
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "password123", "Test User", "1234567890", "Test Address");

        // Mocking: 이미 이메일이 존재한다고 가정하여 true 반환
        Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        // When & Then: IllegalArgumentException이 발생하는지 확인
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> signUpService.signUp(signUpRequest)
        );
        // 예외 메시지가 "Email already exists"인지 확인
        assertEquals("중복된 이메일 입니다.", exception.getMessage());
        log.info("중복 이메일 회원가입 실패 테스트 통과");
    }
    @Test
    void 잘못된_비밀번호_로그인_실패() {
        // Given: 잘못된 비밀번호로 로그인 시도
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");

        // 이미 저장된 사용자를 가정하여 데이터 설정
        String hashedPassword = hashPassword("correctpassword");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(hashedPassword); // 올바른 비밀번호 설정
        user.setRole(UserRole.USER); // 여기에서 UserRole을 설정해 줍니다.

        // Mocking: 이메일로 사용자를 찾는 메서드를 모킹하여 user 객체 반환
        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));

        // When: 잘못된 비밀번호로 로그인 시도
        log.info("잘못된 비밀번호로 로그인 시도: {}", loginRequest.getEmail());
        String token = signUpService.login(loginRequest);

        // Then: 토큰이 null이어야 함을 확인
        assertNull(token);
        log.info("잘못된 비밀번호로 로그인 실패 테스트 통과");
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
        user.setRole(UserRole.USER); // 여기에서 UserRole을 설정해 줍니다.

        // Mocking
        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));

        // When
        log.info("로그인 프로세스 시작: {}", loginRequest.getEmail());
        String token = signUpService.login(loginRequest);

        // Then
        assertNotNull(token);
        assertEquals(UserRole.USER, user.getRole());
        log.info("로그인 프로세스 성공: {}", user.getEmail());
    }
    @Test
    void 비밀번호_변경_후_로그인_성공() {
        // Given: 비밀번호를 변경한 후 새로운 비밀번호로 로그인 시도
        SignUpRequest signUpRequest = new SignUpRequest("test@example.com", "oldpassword", "Test User", "1234567890", "Test Address");

        // 기존 비밀번호를 해싱하여 저장
        String oldHashedPassword = hashPassword("oldpassword");
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(oldHashedPassword); // 해싱된 비밀번호 설정
        user.setName(signUpRequest.getName());
        user.setPhone(signUpRequest.getPhone());
        user.setAddress(signUpRequest.getAddress());
        user.setRole(UserRole.USER);

        // Mocking: 이메일로 사용자를 찾는 메서드를 모킹하여 user 객체 반환
        Mockito.when(userRepository.findByEmail(signUpRequest.getEmail())).thenReturn(java.util.Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // 비밀번호 변경 실행
        userService.updatePassword(user.getId(), new UpdatePasswordRequest("oldpassword", "newpassword"));

        // 새로운 비밀번호로 로그인 시도
        LoginRequest loginRequest = new LoginRequest("test@example.com", "newpassword");
        String token = signUpService.login(loginRequest);

        // Then: 반환된 토큰이 null이 아닌지 확인
        assertNotNull(token);
        log.info("비밀번호 변경 후 로그인 성공: {}", user.getEmail());
    }


    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

}
