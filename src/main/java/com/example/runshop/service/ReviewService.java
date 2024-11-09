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

        Review review = Review.createReview(product, user,
                new ReviewContent(request.getContent().value()),
                new ReviewRating(request.getRating().value()));

        reviewRepository.save(review);
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

    // 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, AddReviewRequest request, Long userId) {
        Review review = findReviewOrThrow(reviewId);
        review.verifyUserPermission(userId);

        review.updateReviewContent(
                new ReviewContent(request.getContent().value()),
                new ReviewRating(request.getRating().value())
        );
    }
    @Transactional
    public void disableReportedReview(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        review.disable();
    }

    private Review findReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰를 찾을 수 없습니다."));
    }
}
