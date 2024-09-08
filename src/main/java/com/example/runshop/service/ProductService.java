package com.example.runshop.service;

import com.example.runshop.config.RoleCheck;
import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.entity.Product;
import com.example.runshop.repository.ProductRepository;
import com.example.runshop.utils.mapper.ProductMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
    @RoleCheck("SELLER")  // "SELLER" 권한만 접근 가능
    public void addProduct(AddProductRequest request) {
        // 상품을 등록하는 코드
        final Product product = new Product(request.getName(), request.getDescription(), request.getPrice(), request.getCategory(), request.getBrand());
        productRepository.save(product);
    }

    // 상품 조회 기능
    public Product getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        return product;
    }

    // 상품 전체 조회 기능
    @Transactional
    public List<ProductDTO> getProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::productToProductDTO)
                .collect(Collectors.toList());
    }

    // 상품 수정 기능
    @RoleCheck("SELLER")  // "ROLE_SELLER" 권한만 접근 가능
    public void updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        Optional.ofNullable(request.getName()).ifPresent(product::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription);
        Optional.of(request.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(request.getCategory()).ifPresent(product::setCategory);
        Optional.ofNullable(request.getBrand()).ifPresent(product::setBrand);

        productRepository.save(product);
    }
    // 상품 삭제 기능

    @RoleCheck("SELLER")  // "ROLE_SELLER" 권한만 접근 가능
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
        productRepository.deleteById(id);
    }

    public void disabled(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
        product.setEnabled(false);
        productRepository.save(product);
    }
}
