package com.example.runshop.filter;

import com.example.runshop.model.dto.user.UsersDetails;
import com.example.runshop.utils.auth.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
class LoginFilterTest {
    private LoginFilter loginFilter;
    private AuthenticationManager authenticationManager;
    private JWT jwt;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwt = mock(JWT.class);
        loginFilter = new LoginFilter(authenticationManager, jwt);
        log.info("LoginFilterTest 설정 완료");
    }

    @Test
    void 유효한_인증정보로_인증을_시도한다() throws Exception {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("email")).thenReturn("user@example.com");
        when(request.getParameter("password")).thenReturn("password");

        Authentication expectedAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(expectedAuth);

        log.info("인증 시도 테스트 시작: user@example.com");

        // When
        Authentication result = loginFilter.attemptAuthentication(request, response);

        // Then
        assertEquals(expectedAuth, result);
        verify(authenticationManager).authenticate(any());
        log.info("인증 시도 테스트 완료: 인증 성공 확인");
    }

    @Test
    void 인증_성공시_JWT토큰을_헤더에_추가한다() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        Authentication auth = mock(Authentication.class);
        UsersDetails userDetails = mock(UsersDetails.class);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        log.info("auth.getPrincipal(),{}",auth.getPrincipal());
        // 빈 권한 목록 설정
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());

        when(jwt.createJwt(anyString(), eq("CUSTOMER"), anyLong())).thenReturn("jwt-token");

        log.info("JWT 토큰 생성 테스트 시작");

        // When
        loginFilter.successfulAuthentication(request, response, chain, auth);

        // Then
        verify(response).addHeader("Authorization", "Bearer jwt-token");
        verify(jwt).createJwt(eq("user@example.com"), eq("CUSTOMER"), anyLong());
        log.info("JWT 토큰 생성 테스트 완료: 헤더에 토큰 추가 확인");
    }

}