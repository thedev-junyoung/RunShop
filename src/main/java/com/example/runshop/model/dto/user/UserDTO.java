package com.example.runshop.model.dto.user;

import com.example.runshop.model.enums.UserRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)  // 부모 클래스의 equals, hashCode 메서드를 포함하도록 설정
public class UserDTO extends BaseUserDTO {
    private Long id;
    private String email;
    private UserRole role;
    private String createdAt;
    private String updatedAt;
    private boolean enabled;
    // 기본 생성자 수동 추가
}
