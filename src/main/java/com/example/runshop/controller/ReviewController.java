package com.example.runshop.controller;

import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.model.dto.review.AddReviewRequest;
import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/products/")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
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
