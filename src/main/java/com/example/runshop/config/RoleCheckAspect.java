package com.example.runshop.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    @Before("@annotation(roleCheck)")
    public void checkRole(RoleCheck roleCheck) throws Throwable {
        log.info("checkRole() in RoleCheckAspect");
        // 인증 확인
        Authentication authentication = getAuthentication();
        Set<String> userRoles = extractUserRoles(authentication);
        Set<String> requiredRoles = extractRequiredRoles(roleCheck);
        log.info("userRoles: {}", userRoles);
        log.info("requiredRoles: {}", requiredRoles);
        // 권한 검증
        verifyRole(userRoles, requiredRoles);
    }

    // 현재 사용자의 인증 정보를 가져오는 메서드
    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("권한이 없습니다.");
        }
        return authentication;
    }

    // 사용자의 권한을 추출하는 메서드
    private Set<String> extractUserRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    // 요구되는 권한을 추출하는 메서드
    private Set<String> extractRequiredRoles(RoleCheck roleCheck) {
        return Arrays.stream(roleCheck.value())
                .collect(Collectors.toSet());
    }

    // 역할 검증 메서드
    private void verifyRole(Set<String> userRoles, Set<String> requiredRoles) {
        boolean hasRole = userRoles.stream()
                .map(role -> role.replace("ROLE_", ""))  // ROLE_ 접두어 제거
                .anyMatch(requiredRoles::contains);
        if (!hasRole) {
            log.info("userRoles: {}", userRoles);
            throw new SecurityException("권한이 없습니다.");
        }
    }
}
