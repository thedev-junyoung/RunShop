package com.example.runshop.service;

import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.enums.Category;

import java.util.List;

public interface ProductService {
    void addProduct(AddProductRequest request);
    void updateProduct(Long productId, UpdateProductRequest request);
    void deleteProduct(Long productId);
    ProductDTO getProduct(Long productId);
    List<ProductDTO> getProducts();
    void disabled(Long productId);
}
