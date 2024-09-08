package com.example.runshop.model.dto.user;

import com.example.runshop.model.entity.User;
import com.example.runshop.model.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class UsersDetails implements UserDetails {
    private final User user; // User 엔티티를 참조하는 필드, 해당 유저의 정보를 저장

    // UsersDetails 생성자, User 객체를 초기화
    public UsersDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 유저의 권한 정보를 담을 Collection 생성
        Collection<GrantedAuthority> collection = new ArrayList<>();

        // 유저의 역할(Role)을 SimpleGrantedAuthority로 감싸서 Collection에 추가
        collection.add(new SimpleGrantedAuthority(""+user.getRole())); // 유저의 역할을 반환

        UserRole role = user.getRole();
        log.info("User's role in UsersDetails: {}", role);  // 역할 확인용 로그
        log.info("collection: {}", collection);
        return collection; // 권한 정보를 담은 Collection을 반환
    }

    @Override
    public String getPassword() {
        // User 엔티티의 비밀번호를 반환
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // User 엔티티의 이메일을 반환 (이메일을 username으로 사용)
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정이 만료되지 않았음을 나타내는 true 반환
        // 계정 만료에 대한 정책이 없으므로 항상 true 반환
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠겨있지 않음을 나타내는 true 반환
        // 계정 잠금에 대한 정책이 없으므로 항상 true 반환
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명(비밀번호)이 만료되지 않았음을 나타내는 true 반환
        // 비밀번호 만료에 대한 정책이 없으므로 항상 true 반환
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정이 활성화되어 있음을 나타내는 true 반환
        // User 엔티티의 enabled 필드 값에 따라 반환
        return user.isEnabled();
    }
}
