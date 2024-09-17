package com.example.runshop.utils;

import com.example.runshop.utils.auth.JWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtTest {

    private JWT jwt;

    @Value("${spring.jwt.secret}")
    private String secretKey; // application.properties에서 설정한 JWT 키

    @BeforeEach
    public void setUp() {
        jwt = new JWT(secretKey); // JWT 객체를 초기화
    }

    @Test
    @DisplayName("토큰 생성 성공")
    public void shouldCreateTokenSuccessfully() {
        // given
        String username = "testuser";
        String role = "ROLE_SELLER";
        Long expirationTime = 1000 * 60 * 60L; // 1시간

        // when
        String token = jwt.createJwt(username, role, expirationTime);

        // then
        assertNotNull(token); // 토큰이 null이 아닌지 확인
    }

    @Test
    @DisplayName("토큰 유효성 검증 성공")
    public void shouldValidateTokenSuccessfully() {
        // given
        String username = "testuser";
        String role = "ROLE_SELLER";
        Long expirationTime = 1000 * 60 * 60L; // 1시간

        // 토큰 생성
        String token = jwt.createJwt(username, role, expirationTime);

        // when
        boolean isValid = jwt.isValidToken(token); // 토큰이 유효한지 확인

        // then
        assertTrue(isValid); // 유효한 토큰인지 확인
    }

    @Test
    @DisplayName("토큰에서 유저 이름 추출 성공")
    public void shouldExtractUsernameFromTokenSuccessfully() {
        // given
        String username = "testuser";
        String role = "ROLE_SELLER";
        Long expirationTime = 1000 * 60 * 60L; // 1시간

        // 토큰 생성
        String token = jwt.createJwt(username, role, expirationTime);

        // when
        String extractedUsername = jwt.getUsername(token);

        // then
        assertEquals(username, extractedUsername); // 토큰에서 추출한 사용자 이름이 올바른지 확인
    }

    @Test
    @DisplayName("토큰에서 역할 추출 성공")
    public void shouldExtractRoleFromTokenSuccessfully() {
        // given
        String username = "testuser";
        String role = "ROLE_SELLER";
        Long expirationTime = 1000 * 60 * 60L; // 1시간

        // 토큰 생성
        String token = jwt.createJwt(username, role, expirationTime);

        // when
        String extractedRole = jwt.getRole(token);

        // then
        assertEquals(role, extractedRole); // 토큰에서 추출한 역할이 올바른지 확인
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 실패")
    public void shouldFailToValidateInvalidToken() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean isValid = jwt.isValidToken(invalidToken);

        // then
        assertFalse(isValid); // 유효하지 않은 토큰임을 확인
    }
}
