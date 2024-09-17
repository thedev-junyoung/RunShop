package com.example.runshop.service;

import com.example.runshop.model.entity.User;
import com.example.runshop.model.vo.user.Email;
import com.example.runshop.model.vo.user.Password;
import com.example.runshop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("사용자가 존재할 때 유저 상세정보를 반환한다")
    void userDetailsWhenUserExists() {
        // Given
        String emailString = "test@example.com";
        Email email = new Email(emailString); // Email VO로 감싸기
        User user = new User();
        user.setEmail(email);
        user.setPassword(new Password("password"));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = loginService.loadUserByUsername(emailString);
        log.info("테스트 시작: 사용자가 존재할 때 유저 상세정보 반환: {}", userDetails);

        // Then
        assertNotNull(userDetails);
        // userDetails.getUsername()는 String을 반환하므로 emailString과 비교
        assertEquals(emailString, userDetails.getUsername());
        log.info("테스트 종료: 유저 상세정보 정상 반환 확인");
    }

    @Test
    @DisplayName("사용자가 존재하지 않을 때 예외를 던진다")
    void ThrowsExceptionWhenUserNotExist() {
        // Given
        String emailString = "nonexistent@example.com";
        Email email = new Email(emailString); // Email VO로 감싸기
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        log.info("테스트 시작: 사용자가 존재하지 않을 때 예외 발생");

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> loginService.loadUserByUsername(emailString));
        log.info("테스트 종료: UsernameNotFoundException 예외 발생 확인");
    }
}