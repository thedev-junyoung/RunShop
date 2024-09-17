package com.example.runshop.filter;

import com.example.runshop.model.dto.user.UsersDetails;
import com.example.runshop.utils.auth.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Collections;

@Slf4j // 로그 출력을 위한 Lombok 애노테이션
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager; // 인증을 관리하는 AuthenticationManager 주입
    private final JWT jwt; // JWT 유틸리티 클래스 주입

    // 생성자에서 AuthenticationManager와 JWT 객체를 주입받음
    public LoginFilter(AuthenticationManager authenticationManager, JWT jwt) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 로그인 시도 시 호출되는 메서드로, 사용자 이름과 비밀번호를 추출하여 인증 토큰을 생성

        String username = obtainUsername(request); // 요청에서 사용자 이름(email)을 추출
        String password = obtainPassword(request); // 요청에서 비밀번호를 추출

        log.info("{} login!", username); // 로그인 시도 로그 출력

        // 사용자 이름과 비밀번호로 인증 토큰 생성 (초기 권한은 빈 리스트)
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());

        // AuthenticationManager를 통해 인증 시도
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        // 사용자 이름 대신 이메일을 username으로 사용
        return request.getParameter("email");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        log.info("successfulAuthentication() in LoginFilter");

        // 인증 성공 시 호출되는 메서드로, JWT 토큰을 생성하여 응답 헤더에 추가

        // 인증 결과에서 사용자 정보를 가져옴
        UsersDetails customUserDetails = (UsersDetails) authResult.getPrincipal();
        String username = customUserDetails.getUsername();

        // 인증된 사용자의 권한을 가져옴
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();

        // 권한이 있다면 첫 번째 권한을 사용하고, 없으면 기본 역할로 "CUSTOMER"를 사용
        String role = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("CUSTOMER");  // 기본 역할 설정

        log.info("role: {}", role); // 사용자 역할 로그 출력

        // JWT 토큰을 생성 (유효기간은 1시간)
        String token = jwt.createJwt(username, role, 60 * 60 * 1000L);
        log.info("JWT token 생성 : " + token); // 생성된 JWT 토큰 로그 출력

        // 응답 헤더에 Authorization 속성으로 JWT 토큰 추가
        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        // 인증 실패 시 호출되는 메서드로, 401 Unauthorized 상태 코드를 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
