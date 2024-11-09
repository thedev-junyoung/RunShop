package com.example.runshop.service;

import com.example.runshop.exception.user.DuplicateEmailException;
import com.example.runshop.exception.user.IncorrectPasswordException;
import com.example.runshop.exception.user.UserNotFoundException;
import com.example.runshop.model.dto.user.*;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.vo.user.Address;
import com.example.runshop.model.vo.user.Email;
import com.example.runshop.model.vo.user.Password;
import com.example.runshop.repository.UserRepository;
import com.example.runshop.utils.auth.SecurityContextUtil;
import com.example.runshop.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service // 이 클래스가 Spring의 서비스 컴포넌트임을 나타냄
@Slf4j // 로그 출력을 위한 Lombok 애노테이션
@Transactional(readOnly = true) // 모든 메서드에 대한 트랜잭션을 읽기 전용으로 설정
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(new Email(request.getEmail()))) {
            throw new DuplicateEmailException("중복된 이메일 입니다.");
        }

        User newUser = User.createUser(request, bCryptPasswordEncoder);
        userRepository.save(newUser);
        log.info("유저 회원가입 성공 : {}", newUser.getEmail());
    }

    // 특정 유저 조회
    public UserDTO getUserById(Long userId) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = findUserOrThrow(userId, "유저 조회");
        return userMapper.userToUserDTO(user);
    }

    // 전체 유저 조회
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::userToUserDTO);
    }
    // 유저 정보 업데이트
    @Transactional()
    public void updateUserDetails(Long userId, UpdateUserRequest request) {
        User user = findUserOrThrow(userId, "유저 정보 수정");
        user.updateUserDetails(request.getName(), request.getPhone(), request.getAddress());
    }

    // 패스워드 업데이트
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = findUserOrThrow(userId, "패스워드 업데이트");
        if (!user.getPassword().matches(bCryptPasswordEncoder, request.getOldPassword())) {
            throw new IncorrectPasswordException("기존 비밀번호가 일치하지 않습니다.");
        }
        // 새로운 비밀번호 설정 및 암호화
        user.changePassword(bCryptPasswordEncoder, request.getOldPassword(), request.getNewPassword());
        log.info("유저 패스워드 변경 완료 userId: {}", userId);
    }

    @Transactional
    public void updateUserRole(Long userId, UpdateRoleRequest request) {
        User user = findUserOrThrow(userId, "유저 역할 수정");
        user.updateRole(request.getRole());
    }

    // 유저 계정 비활성화
    @Transactional
    public void disabled(Long userId) {
        User user = findUserOrThrow(userId, "계정 비활성화");
        user.updateEnabledAccount(false);
    }


    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }


    // 유저 검증 메서드
    public User findUserOrThrow(Long userId, String operationContext) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found. User ID: {}, Operation: {}", userId, operationContext);
                    return new UserNotFoundException(String.format("User not found for operation: %s", operationContext));
                });
    }

}
