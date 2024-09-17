package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor @Builder
@EqualsAndHashCode(callSuper = true)  // 부모 클래스의 equals, hashCode 메서드를 포함하도록 설정
public class AddProductRequest extends BaseProductDTO{

    // 별도의 필드나 추가 검증 로직이 필요한 경우 여기에 추가 가능
    public AddProductRequest(ProductName name, ProductDescription description, ProductPrice price, Category category, String brand) {
        super.setName(name); // VO 사용
        super.setDescription(description); // VO 사용
        super.setPrice(price); // VO 사용
        super.setCategory(category);
        super.setBrand(brand);
    }
}
