package com.example.runshop.utils.customaonotation;

import com.example.runshop.model.vo.product.ProductName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductNameValidator implements ConstraintValidator<ProductNameValid, ProductName> {
    @Override
    public boolean isValid(ProductName productName, ConstraintValidatorContext context) {
        if (productName == null || productName.value() == null) {
            return false;
        }
        String name = productName.value();
        return name.length() >= 3 && name.length() <= 255;  // 길이 제한 체크
    }
}
