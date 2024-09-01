package com.example.shoppingmall.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.shoppingmall.utils.JWT;

import java.io.IOException;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWT jwt;

    public JWTFilter(JWT jwt) {
        this.jwt = jwt;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            // 유효하지 않은 토큰 처리
            if (!jwt.isValidToken(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 401 상태 코드 설정
                return;  // 요청 처리 중단
            }

            // 만료된 토큰 처리
            if (jwt.isExpired(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 401 상태 코드 설정
                return;  // 요청 처리 중단
            }

            String username = jwt.getUsername(token);
            log.info("Authenticated user: " + username);
        }

        filterChain.doFilter(request, response);
    }
}
