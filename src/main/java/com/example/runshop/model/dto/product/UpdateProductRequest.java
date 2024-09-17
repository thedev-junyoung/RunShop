package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)  // 부모 클래스의 equals, hashCode 메서드를 포함하도록 설정
public class UpdateProductRequest extends BaseProductDTO{
    public UpdateProductRequest(String name, String description, BigDecimal price, Category category, String brand) {
        super.setName(name);
        super.setDescription(description);
        super.setPrice(price);
        super.setCategory(category);
        super.setBrand(brand);
    }
}
