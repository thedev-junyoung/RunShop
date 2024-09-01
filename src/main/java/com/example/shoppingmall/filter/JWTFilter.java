package com.example.shoppingmall.filter;

import com.example.shoppingmall.model.dto.user.UsersDetails;
import com.example.shoppingmall.model.entity.User;
import com.example.shoppingmall.model.enums.UserRole;
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
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            log.info("토큰이 없음");
            filterChain.doFilter(request, response);
            //조건이 해당되면 메소드 종료 (필수)
            return;
        }
        String token = authorization.substring(7);
        // 유효하지 않은 토큰 처리
        if (!jwt.isValidToken(token)) {
            log.info("유효하지 않은 토큰 : " + token);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 401 상태 코드 설정
            return;  // 요청 처리 중단
        }
        // 만료된 토큰 처리
        if (jwt.isExpired(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());  // 401 상태 코드 설정
            return;  // 요청 처리 중단
        }

        String username = jwt.getUsername(token);
        String role = jwt.getRole(token);
        User user = new User();
        user.setEmail(username);
        user.setRole(UserRole.valueOf(role));
        log.info("인증된 유저: " + username);

        UsersDetails userDetails = new UsersDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
