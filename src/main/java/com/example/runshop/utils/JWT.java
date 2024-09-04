package com.example.runshop.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Slf4j
@Component // 이 클래스가 Spring의 컴포넌트임을 나타내며, JWT 관련 작업을 처리
public class JWT {

    private static final Logger logger = LoggerFactory.getLogger(JWT.class); // 로그 출력을 위한 Logger 설정
    private final SecretKey secretKey; // JWT 토큰 서명을 위한 비밀키
    private Set<String> invalidatedTokens = new HashSet<>();

    // 생성자에서 비밀키를 초기화
    // secret 애플리케이션 설정에서 주입된 비밀키 값
    public JWT(@Value("${spring.jwt.secret}") String secret) {
        // 비밀키를 UTF-8로 인코딩하여 SecretKeySpec 객체로 생성
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 토큰에서 사용자 이름(username) 추출
    public String getUsername(String token) {
        return Jwts.parser() // 토큰 파서 초기화
                .verifyWith(secretKey) // 비밀키를 사용해 토큰 서명 검증
                .build()
                .parseSignedClaims(token) // 토큰 파싱 및 검증
                .getPayload()
                .get("username", String.class); // "username" 클레임 값 추출
    }

    // 사용자 역할(role) 추출
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class); // "role" 클레임 값 추출
    }

    // 토큰이 만료되었는지 확인
    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().before(new Date()); // 토큰 만료 날짜와 현재 날짜 비교
    }

    /**
     * 토큰의 유효성을 검사
     * @param token JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token); // 토큰 파싱 및 검증
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage()); // 유효하지 않은 토큰에 대한 로그 출력
            return false;
        }
    }

    // JWT 토큰 생성
    public String createJwt(String username, String role, Long expiredMs) {
        logger.info("Creating JWT for user: {}", username);
        return Jwts.builder()
                .claim("username", username) // 사용자 이름을 클레임에 추가
                .claim("role", role) // 사용자 역할을 클레임에 추가
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 설정
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간 설정
                .signWith(secretKey) // 비밀키로 토큰 서명
                .compact(); // 최종 JWT 문자열 생성
    }


    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public boolean isTokenValid(String token) {
        return !invalidatedTokens.contains(token) && isValidToken(token);
    }

    // JWT 토큰에서 사용자 정보 추출 (이메일 또는 사용자 ID)
    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("username", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Unable to get username from token", e);
        }
    }
}
