package com.example.runshop.model.dto.user;

import com.example.runshop.model.enums.UserRole;
import com.example.runshop.model.vo.user.Address;
import com.example.runshop.model.vo.user.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)  // 부모 클래스의 equals, hashCode 메서드를 포함하도록 설정
public class UserDTO extends BaseUserDTO {
    private Long id;
    private Email email;
    private UserRole role;
    private Address address;
    private String createdAt;
    private String updatedAt;
    private boolean enabled;
    private String name;
    private String phone;
    // 기본 생성자 수동 추가
}
