package com.example.runshop.config;

import com.example.runshop.filter.JWTFilter;
import com.example.runshop.filter.LoginFilter;
import com.example.runshop.utils.JWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration // 스프링 설정 클래스임을 나타냄
@EnableWebSecurity

@Slf4j
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWT jwt;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWT jwt) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwt = jwt;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // AuthenticationManager: 인증을 수행하는 인터페이스
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // BCryptPasswordEncoder: 비밀번호를 암호화하기 위한 클래스
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("filterChain() in SecurityConfig");
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // Filter 설정
        // 1. JWTFilter: JWT 토큰을 검증하는 필터
        // 2. LoginFilter: 로그인 요청을 처리하는 필터
        // addFilterBefore: UsernamePasswordAuthenticationFilter 앞에 JWTFilter 추가
        // addFilterAt: UsernamePasswordAuthenticationFilter 위치에 LoginFilter 추가
        // UsernamePasswordAuthenticationFilter: 사용자 이름(email)/암호 인증을 처리하는 인증 필터
        http.addFilterBefore(new JWTFilter(this.jwt), UsernamePasswordAuthenticationFilter.class);
        //http.addFilterAt(new LoginFilter(this.authenticationManager(this.authenticationConfiguration), this.jwt, responseUtils), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwt), UsernamePasswordAuthenticationFilter.class);


        // 세션 생성 정책 설정
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // CORS 설정
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();

            config.setAllowedOrigins(Collections.singletonList("http://localhost:5000"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setMaxAge(3600L);
            config.setExposedHeaders(Collections.singletonList("Authorization"));

            return config;
        }));

        return http.build();
    }
}