package com.example.runshop.service;

import com.example.runshop.model.dto.user.UsersDetails;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.vo.user.Email;
import com.example.runshop.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // 이 클래스가 서비스 계층의 컴포넌트임을 나타냄
public class LoginService implements UserDetailsService {
    // UserDetailsService: Spring Security에서 제공하는 인터페이스, 사용자의 인증 및 인증 정보 로드를 로드하는 메서드를 정의

    private final UserRepository userRepository;

    // 생성자를 통한 의존성 주입
    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Spring Security의 UserDetailsService 인터페이스 구현 메서드
     * 사용자가 로그인 시, 입력한 이메일을 통해 사용자 정보를 로드함
     *
     * @param emailString 사용자가 입력한 이메일 (username 역할)
     * @return UserDetails 인터페이스를 구현한 UsersDetails 객체 반환
     * @throws UsernameNotFoundException 주어진 이메일로 사용자를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String emailString) throws UsernameNotFoundException {
        // 문자열로 받은 이메일을 Email VO로 변환
        Email email = new Email(emailString);

        // 사용자 이메일을 통해 User 엔티티 조회 및 예외 처리
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + emailString));

        // 조회된 사용자 정보를 UserDetails 객체로 변환하여 반환
        return new UsersDetails(user.getEmail(),user.getRole());
    }

}
