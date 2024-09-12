package com.example.runshop.service;

import com.example.runshop.model.entity.User;
import com.example.runshop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
    void 사용자가_존재할때_유저상세정보를_반환한다() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = loginService.loadUserByUsername(email);
        log.info("테스트 시작: 사용자가 존재할 때 유저 상세정보 반환: {}", userDetails);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        log.info("테스트 종료: 유저 상세정보 정상 반환 확인");

    }

    @Test
    void 사용자가_존재하지_않을때_예외를_던진다() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        log.info("테스트 시작: 사용자가 존재하지 않을 때 예외 발생");

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> loginService.loadUserByUsername(email));
        log.info("테스트 종료: UsernameNotFoundException 예외 발생 확인");
    }
}