// 공통 부모 클래스
package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor  // 모든 필드를 사용하는 생성자 추가
public abstract class BaseProductDTO {
    @NotBlank(message = "이름은 필수값입니다.")
    private String name;

    private String description;  // 설명은 필수가 아니므로 검증 없음

    @NotNull(message = "가격은 필수값입니다.")
    @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
    private BigDecimal price;

    @NotNull(message = "카테고리는 필수값입니다.")
    private Category category;

    private String brand;  // 브랜드는 필수가 아니므로 검증 없음
    private boolean enabled = true;  // 기본값은 true

}
