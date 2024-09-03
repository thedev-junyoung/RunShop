package com.example.runshop.service;

import com.example.runshop.exception.user.IncorrectPasswordException;
import com.example.runshop.exception.user.UserNotFoundException;
import com.example.runshop.utils.mapper.UserMapper;
import com.example.runshop.model.dto.user.SignUpRequest;
import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.runshop.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // 이 클래스가 Spring의 서비스 컴포넌트임을 나타냄
@Slf4j // 로그 출력을 위한 Lombok 애노테이션
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    // 생성자를 통한 의존성 주입
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    // 회원가입 처리
    // 회원가입 과정 중 오류 발생 시 모든 변경사항이 롤백되도록 @Transactional 애노테이션 추가
    @Transactional
    public void signUp(SignUpRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("중복된 이메일 입니다.");
        }

        // 새로운 User 엔티티 생성 및 필드 설정
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword())); // 비밀번호 암호화
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        // User 엔티티 저장
        userRepository.save(user);
        log.info("유저 회원가입 성공 : {}", user.getEmail());
    }

    // 특정 유저 조회
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public UserDTO getUserById(Long userId) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId)); // 해당 ID의 사용자가 존재하지 않을 경우 발생하는 예외
        return userMapper.userToUserDTO(user);
    }

    // 전체 유저 조회
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<UserDTO> getAllUsers() {
        // 모든 유저를 조회하여 반환
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }
    // 유저 정보 업데이트
    @Transactional
    public void updateUserDetails(Long userId, UpdateUserRequest request) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // 사용자 정보 업데이트
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        /*
         * JPA는 엔티티를 수정할 때, 해당 엔티티의 모든 필드를 업데이트함
         * 만약 일부 필드만 업데이트하고 싶다면, 해당 필드만 수정하고(set) save() 메서드를 호출하면 됨
         */

        // 변경된 User 엔티티 저장
        userRepository.save(user);
        log.info("유저 상세 정보 수정 완료 userId: {}", userId);
    }

    // 패스워드 업데이트
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // 기존 패스워드가 일치하는지 확인
        if (!checkPassword(request.getOldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호 설정 및 암호화
        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));

        // 변경된 User 엔티티 저장
        userRepository.save(user);
        log.info("유저 패스워드 변경 완료 userId: {}", userId);
    }

    // 유저 계정 비활성화
    @Transactional
    public void deactivateUser(Long userId) {
        // userId로 유저를 조회하고, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        // 계정 비활성화 (enabled를 false로 설정)
        user.setEnabled(false);

        // 변경된 User 엔티티 저장
        userRepository.save(user);
        log.info("유저 계정 비활성화 완료 userId: {}", userId);
    }

    // 비밀번호 일치 여부 확인
    private boolean checkPassword(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed); // 주어진 평문 비밀번호와 저장된 해시된 비밀번호를 비교
    }

}
