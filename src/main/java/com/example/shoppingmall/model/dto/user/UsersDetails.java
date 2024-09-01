package com.example.shoppingmall.model.dto.user;

import com.example.shoppingmall.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UsersDetails implements UserDetails {
    private final User user;

    public UsersDetails(User user) {
        this.user = user;
    }


    //
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 유저의 권한 정보를 담을 Collection 생성
        Collection<GrantedAuthority> collection = new ArrayList<>();
        // 유저의 권한을 GrantedAuthority로 변환하여 Collection에 추가
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return String.valueOf(user.getRole());
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
    @Override
    public boolean isAccountNonExpired() {
        // 계정이 만료되지 않았음을 나타내는 true 반환
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠겨있지 않음을 나타낼 때
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        // 예: 비밀번호를 90일마다 변경해야 할 때
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정이 활성화되어 있음을 나타내는 true 반환
        return user.isEnabled();
    }

}
