package com.example.runshop.model.dto.product;

import com.example.runshop.model.enums.Category;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class ProductDTO extends BaseProductDTO{
    private Long id;
    private LocalDateTime createdAt;  // 추가
    private LocalDateTime updatedAt;  // 추가

}
