package com.example.runshop.service;

import com.example.runshop.exception.product.ProductNotFoundException;
import com.example.runshop.exception.review.NotHavePermissionReviewException;
import com.example.runshop.exception.review.ReviewNotFoundException;
import com.example.runshop.exception.user.UserNotFoundException;
import com.example.runshop.model.dto.review.AddReviewRequest;
import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.model.entity.Product;
import com.example.runshop.model.entity.Review;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.vo.review.ReviewContent;
import com.example.runshop.model.vo.review.ReviewRating;
import com.example.runshop.repository.ProductRepository;
import com.example.runshop.repository.ReviewRepository;
import com.example.runshop.repository.UserRepository;
import com.example.runshop.utils.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    public void addReview(Long productId, AddReviewRequest request) {
        log.info("Add Review Request: {}", request);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // 리뷰 생성 및 저장
        Review review = createReview(product, user, request);
        reviewRepository.save(review);
    }

    private Review createReview(Product product, User user, AddReviewRequest request) {
        return Review.builder()
                .product(product)
                .user(user)
                .rating(new ReviewRating(request.getRating().value()))
                .content(new ReviewContent(request.getContent().value()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviewMapper.riciewToReviewDTOList(reviews);
    }

    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        return reviewMapper.reviewToReviewDTO(review);
    }

    @Transactional
    public void updateReview(Long reviewId, AddReviewRequest request, Long userId) {
        Review review = findReviewOrThrow(reviewId);
        checkPermission(userId, review);

        updateReviewContent(review, request);
    }

    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        // 리뷰 조회
        Review review = findReviewOrThrow(reviewId);
        // 소프트 삭제 - 리뷰를 비활성화
        review.setEnabled(false);
    }

    private void updateReviewContent(Review review, AddReviewRequest request) {
        Optional.ofNullable(request.getContent()).ifPresent(newContent -> review.setContent(new ReviewContent(newContent.value())));
        Optional.ofNullable(request.getRating()).ifPresent(newRating -> review.setRating(new ReviewRating(newRating.value())));
    }

    private void checkPermission(Long userId, Review review) {
        if (!review.getUser().getId().equals(userId)) {
            throw new NotHavePermissionReviewException("해당 리뷰에 대한 수정 권한이 없습니다.");
        }
    }

    private Review findReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰를 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = findReviewOrThrow(reviewId);
        checkPermission(userId, review);
        review.setEnabled(false);
        reviewRepository.save(review);  // 명시적으로 호출

    }

    @Transactional
    public void disableReviewByAdmin(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        // 소프트 삭제 - 리뷰를 비활성화
        review.setEnabled(false);
        reviewRepository.save(review);  // 명시적으로 호출
    }

    public void deleteReportedReview(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        review.setEnabled(false);
    }
}
