package com.example.runshop.utils.customaonotation;

import com.example.runshop.model.vo.product.ProductName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductNameValidator implements ConstraintValidator<ProductNameValid, ProductName> {
    @Override
    public boolean isValid(ProductName productName, ConstraintValidatorContext context) {
        if (productName == null || productName.getValue() == null) {
            return false;
        }
        String name = productName.getValue();
        return name.length() >= 3 && name.length() <= 255;  // 길이 제한 체크
    }
}
