package com.example.runshop.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JwtLogoutHandler implements LogoutHandler {
    private final JWT jwt;
    // application.properties에서 비밀키를 주입받음
    @Value("${spring.jwt.secret}")
    private SecretKey secretKey;
    public JwtLogoutHandler(JWT jwt) {
        this.jwt = jwt;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = extractToken(request);
        log.info("로그아웃 처리: token={}", token);
        if (token != null) {
            String username = jwt.getUsernameFromToken(token); // 토큰에서 사용자 정보 추출
            log.info("로그아웃한 사용자: username={}", username); // 사용자 정보 로그 출력

            jwt.invalidateToken(token); // 토큰 무효화 처리
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}