// 공통 부모 클래스
package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseProductDTO {
    private ProductName name;
    private ProductDescription description;  // 설명은 필수가 아니므로 검증 없음

    @NotNull(message = "가격은 필수값입니다.")
    private ProductPrice price;

    @NotNull(message = "카테고리는 필수값입니다.")
    private Category category;

    private String brand;  // 브랜드는 필수가 아니므로 검증 없음

    @Builder.Default
    private boolean enabled = true;  // 기본값은 true

}
