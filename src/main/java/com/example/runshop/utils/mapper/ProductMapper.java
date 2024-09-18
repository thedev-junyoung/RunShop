package com.example.runshop.utils.mapper;

import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "description", source = "description")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "mapDate")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "mapDate")
    ProductDTO productToProductDTO(Product product);

    default String map(ProductName productName) {
        return productName.value();
    }

    default BigDecimal map(ProductPrice productPrice) {
        return productPrice.value();
    }

    default String map(ProductDescription productDescription) {
        return productDescription.value();
    }
    @Named("mapDate")
    default String mapDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
