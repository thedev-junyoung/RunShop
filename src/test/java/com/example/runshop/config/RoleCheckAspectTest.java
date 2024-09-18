package com.example.runshop.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {RoleCheckAspect.class})  // 필요한 설정 클래스를 추가
public class RoleCheckAspectTest {

    @Test
    public void 유효한_역할_역할_확인_테스트() {
        // Given: 가짜 SecurityContext를 설정하여 SELLER 역할을 가진 사용자로 가정
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        // 권한 목록을 명확하게 타입 설정
        // 권한 목록을 명확하게 타입 설정
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("SELLER"));
        // Untyped 캐스팅으로 Mockito에게 제네릭 타입을 명시적으로 지정
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When: 실제로 AOP가 트리거되는 상황을 가정하여 메서드 호출 (RoleCheck가 적용된 메서드 호출)
        RoleCheckAspect roleCheckAspect = new RoleCheckAspect();
        RoleCheck roleCheck = mock(RoleCheck.class);
        when(roleCheck.value()).thenReturn(new String[]{"SELLER"});

        // Then: 권한 검증 성공
        assertDoesNotThrow(() -> roleCheckAspect.checkRole(roleCheck));

        // Cleanup: SecurityContextHolder 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    public void 잘못된_역할이_있는지_테스트_역할() {
        // Given: 가짜 SecurityContext를 설정하여 CUSTOMER 역할을 가진 사용자로 가정
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        // 권한 목록을 명확하게 타입 설정
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("CUSTOMER"));

        // Untyped 캐스팅으로 Mockito에게 제네릭 타입을 명시적으로 지정
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);


        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When: 실제로 AOP가 트리거되는 상황을 가정하여 메서드 호출
        RoleCheckAspect roleCheckAspect = new RoleCheckAspect();
        RoleCheck roleCheck = mock(RoleCheck.class);
        when(roleCheck.value()).thenReturn(new String[]{"SELLER"});

        // Then: 권한 검증 실패 시 SecurityException 발생
        SecurityException exception = assertThrows(SecurityException.class, () -> roleCheckAspect.checkRole(roleCheck));
        assertEquals("권한이 없습니다.", exception.getMessage());

        // Cleanup: SecurityContextHolder 초기화
        SecurityContextHolder.clearContext();
    }
}
