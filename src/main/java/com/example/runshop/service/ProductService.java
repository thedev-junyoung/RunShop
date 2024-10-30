package com.example.runshop.service;

import com.example.runshop.exception.product.ProductNotFoundException;
import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.entity.Product;
import com.example.runshop.repository.ProductRepository;
import com.example.runshop.utils.mapper.ProductMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public void addProduct(AddProductRequest request) {
        // 상품을 등록하는 코드
        final Product product = Product.builder()
                .name(request.getName()) // VO 사용
                .description(request.getDescription()) // VO 사용
                .price(request.getPrice()) // VO 사용
                .category(request.getCategory())
                .brand(request.getBrand())
                .build();
        productRepository.save(product);
    }

    // 상품 조회 기능
    @Transactional(readOnly = true)
    @Cacheable(value = "productCache", key = "#id")
    public ProductDTO getProduct(Long id) {
        Product product = findProductOrThrow(id);
        return productMapper.productToProductDTO(product);
    }

    // 상품 전체 조회 기능
    @Transactional(readOnly = true)
    @Cacheable(value = "productListCache")
    public Page<ProductDTO> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::productToProductDTO);
    }
    // 상품 수정 기능
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "productCache", key = "#id") // 해당 상품 캐시 삭제
    public void updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);

        Optional.ofNullable(request.getName()).ifPresent(product::setName); // VO 사용
        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription); // VO 사용
        Optional.ofNullable(request.getPrice()).ifPresent(product::setPrice); // VO 사용
        Optional.ofNullable(request.getCategory()).ifPresent(product::setCategory);
        Optional.ofNullable(request.getBrand()).ifPresent(product::setBrand);

        productRepository.save(product);
    }

    // 상품 삭제 기능
    @Transactional
    @RoleCheck("SELLER") // "SELLER" 권한만 접근 가능
    public void deleteProduct(Long id) {
        findProductOrThrow(id);
        productRepository.deleteById(id);
    }

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
