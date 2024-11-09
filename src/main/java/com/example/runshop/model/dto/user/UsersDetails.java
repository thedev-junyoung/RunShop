package com.example.runshop.model.dto.user;

import com.example.runshop.model.enums.UserRole;
import com.example.runshop.model.vo.user.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Slf4j
public class UsersDetails implements UserDetails {

    private final Email email;
    private final UserRole role;

    public UsersDetails(Email email, UserRole role) {
        this.email = email;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return email.value();
    }

    @Override
    public String getPassword() {
        return null; // JWT 인증 방식에서는 패스워드가 필요 없음
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
