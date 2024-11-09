package com.example.runshop.model.entity;

import com.example.runshop.exception.user.IncorrectPasswordException;
import com.example.runshop.model.dto.user.SignUpRequest;
import com.example.runshop.model.vo.user.Address;
import com.example.runshop.model.vo.user.Email;
import com.example.runshop.model.vo.user.Password;
import com.example.runshop.model.enums.UserRole;
import com.example.runshop.module.order.domain.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // 기본 생성자는 JPA용으로만 사용 가능하도록 보호
@AllArgsConstructor
@Table(name = "users")
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
@Slf4j
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "email"))
    })
    private Email email;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "password"))
    })
    private Password password;

    @Column(name= "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @Embedded
    private Address address;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "dtype", nullable = false)
    @Builder.Default  // Builder에서 기본값 설정
    private UserRole role = UserRole.CUSTOMER;

    @Column(name = "enabled")
    @Builder.Default  // Builder에서 기본값 설정
    private boolean enabled = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder.Default  // Builder에서 기본값 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @Builder.Default  // Builder에서 기본값 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();


    public void setApproved(boolean approved) {
        if (approved) {
            this.role = UserRole.SELLER;
        }
    }
    public boolean isApproved() {
        return this.role == UserRole.SELLER;
    }



    public static User createUser(SignUpRequest request, BCryptPasswordEncoder encoder) {
        return User.builder()
                .email(new Email(request.getEmail()))
                .password(new Password(encoder.encode(request.getPassword())))
                .name(request.getName())
                .phone(request.getPhone())
                .address(new Address(
                        request.getAddress().street(),
                        request.getAddress().detailedAddress(),
                        request.getAddress().city(),
                        request.getAddress().region(),
                        request.getAddress().zipCode()
                ))
                .role(UserRole.CUSTOMER) // 기본 역할 설정
                .enabled(true) // 기본 활성화 상태 설정
                .build();
    }



    // 비즈니스 로직 메서드 추가
    public void changePassword(BCryptPasswordEncoder encoder, String oldPassword, String newPassword) {
        if (!encoder.matches(oldPassword, this.password.value())) {
            throw new IncorrectPasswordException("기존 비밀번호가 일치하지 않습니다.");
        }
        this.password = new Password(encoder.encode(newPassword));
    }

    public void updateEnabledAccount(boolean enabled) {
        this.enabled = enabled;
    }

    public void updateUserDetails(String name, String phone, Address address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public void updateRole(UserRole newRole) {
        this.role = newRole;
    }


    // 판매자용 팩토리 메서드
    public static User createSeller(SignUpRequest request, BCryptPasswordEncoder encoder) {
        return User.builder()
                .email(new Email(request.getEmail()))
                .password(new Password(encoder.encode(request.getPassword())))
                .name(request.getName())
                .phone(request.getPhone())
                .address(new Address(
                        request.getAddress().street(),
                        request.getAddress().detailedAddress(),
                        request.getAddress().city(),
                        request.getAddress().region(),
                        request.getAddress().zipCode()
                ))
                .role(UserRole.SELLER)
                .enabled(false)  // 판매자는 승인 전까지 비활성화
                .build();
    }



}
