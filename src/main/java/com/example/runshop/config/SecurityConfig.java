package com.example.runshop.config;

import com.example.runshop.filter.JWTFilter;
import com.example.runshop.filter.LoginFilter;
import com.example.runshop.utils.auth.JWT;
import com.example.runshop.utils.auth.JwtLogoutHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration // 이 클래스가 스프링 설정 클래스임을 나타냄
@EnableWebSecurity // 웹 보안 활성화를 위한 애노테이션
@Slf4j // 로깅 기능을 위한 Lombok 애노테이션
public class SecurityConfig {

    // AuthenticationConfiguration: 인증 설정을 관리하는 클래스, 인증을 처리하는 AuthenticationManager 제공
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWT jwt;

    // 생성자 주입을 통해 AuthenticationConfiguration과 JWT 객체를 주입받음
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWT jwt) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwt = jwt;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // AuthenticationManager 빈을 생성하여 반환
        // AuthenticationManager는 인증을 처리하는 핵심 컴포넌트
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // 비밀번호를 안전하게 저장하기 위해 BCrypt 해시 함수를 사용하는 빈을 생성하여 반환
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("filterChain() in SecurityConfig"); // 필터 체인 설정 메서드 호출 로그

        // CSRF 보호를 비활성화, 폼 로그인과 HTTP 기본 인증을 비활성화
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // 특정 엔드포인트를 허용: 회원가입, 로그인 엔드포인트 등
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/signup", "/login","/h2-console/**").permitAll() // 이 경로들은 인증 없이 접근 허용
                .anyRequest().authenticated() // 나머지 요청들은 인증 필요
        )            .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                        .addLogoutHandler(new JwtLogoutHandler(jwt))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;
        // 커스텀 필터 추가
        // JWTFilter: JWT 토큰을 검증하는 필터로, 사용자 인증 정보를 추출하고 검증함
        // LoginFilter: 로그인 요청을 처리하여 사용자 인증을 수행하는 필터
        // addFilterBefore: UsernamePasswordAuthenticationFilter 앞에 JWTFilter를 추가
        // addFilterAt: UsernamePasswordAuthenticationFilter 위치에 LoginFilter를 추가
        http.addFilterBefore(new JWTFilter(this.jwt), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwt), UsernamePasswordAuthenticationFilter.class);

        // 세션을 사용하지 않도록 Stateless로 설정
        // 이는 REST API에 적합하며, 서버가 클라이언트의 상태를 유지하지 않음
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // CORS 설정: 특정 도메인에서 오는 요청만 허용하고, 기타 CORS 관련 설정을 진행
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();

            config.setAllowedOrigins(Collections.singletonList("http://localhost:5000")); // 허용할 출처
            config.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드 허용
            config.setAllowCredentials(true); // 자격 증명 허용 (쿠키 등)
            config.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
            config.setMaxAge(3600L); // 1시간 동안 캐시
            config.setExposedHeaders(Collections.singletonList("Authorization")); // 클라이언트가 접근할 수 있는 헤더

            return config;
        }));

        // 최종적으로 설정된 SecurityFilterChain을 반환
        return http.build();
    }
}
