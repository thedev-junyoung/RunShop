package com.example.runshop.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor  // 모든 필드를 사용하는 생성자 추가
public abstract class BaseUserDTO {
    private String name;
    private String phone;
    private String address;
}