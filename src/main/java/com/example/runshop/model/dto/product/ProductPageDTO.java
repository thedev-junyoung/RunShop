package com.example.runshop.model.dto.product;

import lombok.Data;
import java.util.List;

@Data
public class ProductPageDTO {
    private List<ProductDTO> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private long totalPages;

    public ProductPageDTO(List<ProductDTO> content, int pageNumber, int pageSize, long totalElements, long totalPages) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
