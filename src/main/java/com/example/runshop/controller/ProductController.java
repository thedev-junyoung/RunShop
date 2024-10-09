package com.example.runshop.controller;

import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody @Valid AddProductRequest request, HttpServletRequest httpRequest) {
        productService.addProduct(request);
        return SuccessResponse.ok("상품이 성공적으로 등록되었습니다.", httpRequest.getRequestURI());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id, HttpServletRequest httpRequest) {
        ProductDTO product = productService.getProduct(id);
        return SuccessResponse.ok("상품을 성공적으로 조회했습니다.", product, httpRequest.getRequestURI());
    }

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest httpRequest) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.getProducts(pageable);
        return SuccessResponse.ok("상품 목록을 성공적으로 조회했습니다.", products, httpRequest.getRequestURI());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest request, HttpServletRequest httpRequest) {
        productService.updateProduct(id, request);
        return SuccessResponse.ok("상품이 성공적으로 수정되었습니다.", httpRequest.getRequestURI());
    }

    @PatchMapping("/{id}/disabled")
    public ResponseEntity<?> deactivateProduct(@PathVariable Long id, HttpServletRequest httpRequest) {
        productService.disabled(id);
        return SuccessResponse.ok("상품이 성공적으로 비활성화되었습니다.", httpRequest.getRequestURI());
    }

}
