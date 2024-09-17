package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)  // 부모 클래스의 equals, hashCode 메서드를 포함하도록 설정
public class AddProductRequest extends BaseProductDTO{

    // 별도의 필드나 추가 검증 로직이 필요한 경우 여기에 추가 가능
    public AddProductRequest(String name, String description, BigDecimal price, Category category, String brand) {
        super.setName(name);
        super.setDescription(description);
        super.setPrice(price);
        super.setCategory(category);
        super.setBrand(brand);
    }
}
