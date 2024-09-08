package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)  // 부모 클래스의 equals, hashCode 메서드를 포함하도록 설정
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class ProductDTO extends BaseProductDTO{
    private Long id;
    private LocalDateTime createdAt;  // 추가
    private LocalDateTime updatedAt;  // 추가
}
