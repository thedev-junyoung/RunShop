package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)  // 부모 클래스의 equals, hashCode 메서드를 포함하도록 설정
@NoArgsConstructor
public class ProductDTO extends BaseProductDTO{
    private Long id;
}
