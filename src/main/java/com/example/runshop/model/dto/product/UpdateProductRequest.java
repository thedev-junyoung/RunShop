package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)  // 부모 클래스의 equals, hashCode 메서드를 포함하도록 설정
@NoArgsConstructor // 기본 생성자 추가
public class UpdateProductRequest extends BaseProductDTO{
    public UpdateProductRequest(ProductName name, ProductDescription description, ProductPrice price, Category category, String brand) {
        super.setName(name);
        super.setDescription(description);
        super.setPrice(price);
        super.setCategory(category);
        super.setBrand(brand);
    }
}
