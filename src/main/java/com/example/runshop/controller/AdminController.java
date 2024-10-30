package com.example.runshop.controller;

import com.example.runshop.model.dto.product.ProductDTO;
import com.example.runshop.model.dto.response.SuccessResponse;
import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.service.AdminService;
import com.example.runshop.service.ProductService;
import com.example.runshop.service.ReviewService;
import com.example.runshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final ProductService productService;

    public AdminController(AdminService adminService, UserService userService, ReviewService reviewService, ProductService productService) {
        this.adminService = adminService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.productService = productService;
    }

    // [추가] 페이징 적용된 회원 목록 조회 API
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      HttpServletRequest httpRequest) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return SuccessResponse.ok("회원 목록을 성공적으로 조회했습니다.", users, httpRequest.getRequestURI());
    }

    // [추가] 리뷰 신고 관리 API
    @GetMapping("/reported-reviews")
    public ResponseEntity<?> getReportedReviews(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                HttpServletRequest httpRequest) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reportedReviews = adminService.getReportedReviews(pageable);
        return SuccessResponse.ok("신고된 리뷰 목록을 성공적으로 조회했습니다.", reportedReviews, httpRequest.getRequestURI());
    }

    // [추가] 신고된 리뷰 삭제 API
    @DeleteMapping("/reported-reviews/{reviewId}")
    public ResponseEntity<?> deleteReportedReview(@PathVariable Long reviewId, HttpServletRequest httpRequest) {
        reviewService.deleteReportedReview(reviewId);
        return SuccessResponse.ok("신고된 리뷰가 성공적으로 삭제되었습니다.", httpRequest.getRequestURI());
    }

    // [추가] 페이징 적용된 상품 목록 조회 API
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest httpRequest) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.getProducts(pageable);
        return SuccessResponse.ok("상품 목록을 성공적으로 조회했습니다.", products, httpRequest.getRequestURI());
    }

    // 판매자 승인 API
    @PatchMapping("/approve-seller/{sellerId}")
    public ResponseEntity<?> approveSeller(@PathVariable Long sellerId, HttpServletRequest httpRequest) {
        adminService.approveSeller(sellerId);
        return SuccessResponse.ok("판매자가 성공적으로 승인되었습니다.", httpRequest.getRequestURI());
    }

    // 특정 사용자 조회 API
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId, HttpServletRequest httpRequest) {
        UserDTO user = adminService.getUser(userId);
        return SuccessResponse.ok("사용자 정보를 성공적으로 조회했습니다.", user, httpRequest.getRequestURI());
    }

    // 특정 사용자 상태 관리 API (활성화/비활성화)
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<?> manageUserStatus(@PathVariable Long userId,
                                              @RequestParam boolean isEnabled,
                                              HttpServletRequest httpRequest) {
        adminService.manageUserStatus(userId, isEnabled);
        return SuccessResponse.ok("사용자 상태가 성공적으로 변경되었습니다.", httpRequest.getRequestURI());
    }

    // 특정 리뷰 삭제 API (활성화 상태만 false로 설정)
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, HttpServletRequest httpRequest) {
        adminService.deleteReview(reviewId);
        return SuccessResponse.ok("리뷰가 성공적으로 비활성화되었습니다.", httpRequest.getRequestURI());
    }
}
