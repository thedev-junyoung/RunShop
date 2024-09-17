package com.example.runshop.filter;

import com.example.runshop.model.dto.user.UsersDetails;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.runshop.utils.auth.JWT;

import java.io.IOException;

@Slf4j // 로그 출력을 위한 Lombok 애노테이션
public class JWTFilter extends OncePerRequestFilter {

    private final JWT jwt; // JWT 유틸리티 클래스를 주입받아 토큰 관련 작업에 사용

    public JWTFilter(JWT jwt) {
        this.jwt = jwt; // 생성자에서 JWT 객체를 초기화
    }

    // 필터에서 매 요청마다 호출되며, 보안 작업(토큰 검증, 인증 정보 설정 등)을 수행한 후 요청을 다음 필터로 넘기는 역할
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 요청의 Authorization 헤더에서 토큰을 추출
        String authorization = request.getHeader("Authorization");

        // 토큰이 없거나 Bearer로 시작하지 않으면 로그를 남기고 필터 체인을 계속 진행
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("토큰이 없음");
            filterChain.doFilter(request, response);
            return; // 메서드를 종료하여 더 이상 작업을 진행하지 않음
        }

        // Bearer 접두사를 제거하여 순수한 토큰 값만 추출
        String token = authorization.substring(7);

        // 토큰이 유효하지 않은 경우, 로그를 남기고 401 Unauthorized 상태를 설정한 후 요청 처리를 중단
        if (!jwt.isValidToken(token)) {
            log.info("유효하지 않은 토큰 : " + token);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // 토큰이 만료된 경우, 401 Unauthorized 상태를 설정하고 요청 처리를 중단
        if (jwt.isExpired(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // 토큰에서 사용자 이름과 역할을 추출
        String username = jwt.getUsername(token);
        String role = jwt.getRole(token);

        // 사용자 엔터티를 생성하고, 이메일과 역할을 설정
        User user = new User();
        user.setEmail(username);
        user.setRole(UserRole.valueOf(role));
        log.info("인증된 유저: " + username);
        log.info("인증된 역할: " + role);

        // UserDetails 객체를 생성하고, 이를 기반으로 Authentication 객체를 생성
        UsersDetails userDetails = new UsersDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 인증 정보를 설정하여 이후의 필터나 요청에서 이 사용자 정보가 활용되도록 함
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 필터 체인의 다음 필터로 요청을 전달하여 나머지 처리를 계속 진행
        filterChain.doFilter(request, response);
    }
}
