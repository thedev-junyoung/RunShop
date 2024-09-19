package com.example.runshop.controller;

import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.product.UpdateProductRequest;
import com.example.runshop.model.dto.product.AddProductRequest;
import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.model.dto.review.AddReviewRequest;
import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.service.ProductService;
import com.example.runshop.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    public ProductController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
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


    // [추가] 특정 상품의 리뷰 목록 조회 API
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<?> getReviewsByProductId(@PathVariable Long productId, HttpServletRequest httpRequest) {
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        return SuccessResponse.ok("리뷰 목록을 성공적으로 조회했습니다.", reviews, httpRequest.getRequestURI());
    }

    // [추가] 특정 리뷰 수정 API
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @RequestBody @Valid AddReviewRequest request,
                                          @RequestParam Long userId,
                                          HttpServletRequest httpRequest) {
        reviewService.updateReview(reviewId, request, userId);
        return SuccessResponse.ok("리뷰가 성공적으로 수정되었습니다.", httpRequest.getRequestURI());
    }

    // [추가] 특정 리뷰 삭제 API
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId,
                                          @RequestParam Long userId,
                                          HttpServletRequest httpRequest) {
        reviewService.deleteReview(reviewId, userId);
        return SuccessResponse.ok("리뷰가 성공적으로 삭제되었습니다.", httpRequest.getRequestURI());
    }
}
