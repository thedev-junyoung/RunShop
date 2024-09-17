package com.example.runshop.service;

import com.example.runshop.config.RoleCheck;
import com.example.runshop.exception.product.ProductNotFoundException;
import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.entity.Product;
import com.example.runshop.repository.ProductRepository;
import com.example.runshop.utils.mapper.ProductMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService{
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
    @RoleCheck("SELLER")  // "SELLER" 권한만 접근 가능
    @Transactional
    public void addProduct(AddProductRequest request) {
        // 상품을 등록하는 코드
        final Product product = Product
                .builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .brand(request.getBrand())
                .build();
        productRepository.save(product);
    }

    // 상품 조회 기능
    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        Product product = findProductOrThrow(id);

        return productMapper.productToProductDTO(product);
    }


    // 상품 전체 조회 기능
    @Transactional(readOnly = true)
    public List<ProductDTO> getProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::productToProductDTO)
                .collect(Collectors.toList());
    }

    // 상품 수정 기능
    // 상품 수정 과정 중 오류 발생 시 모든 변경사항이 롤백되도록 @Transactional 애노테이션 추가
    // REQUIRES_NEW: 새로운 트랜잭션을 시작하고, 부모 트랜잭션과 독립적으로 실행
    // REPEATABLE_READ: 트랜잭션 내에서 SELECT 쿼리를 여러 번 실행해도 항상 같은 결과를 보장
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @RoleCheck("SELLER")  // "ROLE_SELLER" 권한만 접근 가능
    public void updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);

        Optional.ofNullable(request.getName()).ifPresent(product::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription);
        Optional.of(request.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(request.getCategory()).ifPresent(product::setCategory);
        Optional.ofNullable(request.getBrand()).ifPresent(product::setBrand);

        productRepository.save(product);
    }
    // 상품 삭제 기능


    @RoleCheck("SELLER")  // "ROLE_SELLER" 권한만 접근 가능
    @Transactional
    public void deleteProduct(Long id) {
        findProductOrThrow(id);
        productRepository.deleteById(id);
    }
    @RoleCheck("SELLER")  // "ROLE_SELLER" 권한만 접근 가능
    @Transactional
    public void disabled(Long id) {
        Product product = findProductOrThrow(id);
        product.setEnabled(false);
        productRepository.save(product);
    }


    public Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));
    }
}
