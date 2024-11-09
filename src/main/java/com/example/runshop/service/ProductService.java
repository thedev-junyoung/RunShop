package com.example.runshop.service;

import com.example.runshop.exception.product.ProductNotFoundException;
import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.vo.product.ProductDescription;
import com.example.runshop.model.vo.product.ProductName;
import com.example.runshop.model.vo.product.ProductPrice;
import com.example.runshop.repository.ProductRepository;
import com.example.runshop.utils.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional
    public void addProduct(AddProductRequest request) {
        Product product = Product.builder()
                .name(new ProductName(request.getName().value()))
                .description(new ProductDescription(request.getDescription().value()))
                .price(new ProductPrice(request.getPrice().value()))
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
    @Cacheable(value = "productListCache", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public List<ProductDTO> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::productToProductDTO).getContent();
    }
    // 상품 수정 기능
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @CacheEvict(value = "productCache", key = "#id") // 해당 상품 캐시 삭제
    public void updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);
        product.updateProduct(
                new ProductName(request.getName().value()),
                new ProductDescription(request.getDescription().value()),
                new ProductPrice(request.getPrice().value()),
                request.getCategory(),
                request.getBrand()
        );
    }

    // 상품 삭제 기능
    @Transactional
    public void deleteProduct(Long id) {
        findProductOrThrow(id);
        productRepository.deleteById(id);
    }

    @Transactional
    public void disabled(Long id) {
        Product product = findProductOrThrow(id);
        product.disableProduct();
    }

    public Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));
    }
}
