package com.example.runshop.utils;

import com.example.runshop.model.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class SecurityContextUtil {

    // 권한을 업데이트하고 SecurityContext를 갱신하는 static 메서드
    public static void updateAuthentication(UserRole newRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 새로운 권한 생성
        List<GrantedAuthority> updatedAuthorities = List.of(new SimpleGrantedAuthority(String.valueOf(newRole)));

        // 새로운 인증 객체로 업데이트
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                updatedAuthorities
        );

        // SecurityContext에 새로운 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
