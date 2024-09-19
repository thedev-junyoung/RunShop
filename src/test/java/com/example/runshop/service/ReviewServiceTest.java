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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("추가 검토 성공")
    void testAddReviewSuccess() {
        Long productId = 1L;
        AddReviewRequest request = new AddReviewRequest(1L, new ReviewContent("Great product!"), new ReviewRating(5));

        Product product = mock(Product.class);
        User user = mock(User.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));

        reviewService.addReview(productId, request);

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("추가 리뷰 제품을 찾을 수 없습니다")
    void testAddReviewProductNotFound() {
        Long productId = 1L;
        AddReviewRequest request = new AddReviewRequest(1L, new ReviewContent("Nice product"), new ReviewRating(5));

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> reviewService.addReview(productId, request));
    }

    @Test
    @DisplayName("추가 리뷰 사용자를 찾을 수 없습니다")
    void testAddReviewUserNotFound() {
        Long productId = 1L;
        AddReviewRequest request = new AddReviewRequest(1L, new ReviewContent("Awesome product"), new ReviewRating(5));

        Product product = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.addReview(productId, request));
    }

    @Test
    @DisplayName("리뷰 목록 성공")
    void testGetReviewByIdSuccess() {
        Long reviewId = 1L;
        Review review = mock(Review.class);
        ReviewDTO reviewDTO = mock(ReviewDTO.class);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewMapper.reviewToReviewDTO(review)).thenReturn(reviewDTO);

        ReviewDTO result = reviewService.getReviewById(reviewId);

        assertEquals(reviewDTO, result);
    }

    @Test
    @DisplayName("리뷰 ID를 찾을 수 없습니다")
    void testGetReviewByIdNotFound() {
        Long reviewId = 1L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(reviewId));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void testUpdateReviewSuccess() {
        Long reviewId = 1L;
        Long userId = 1L;
        AddReviewRequest request = new AddReviewRequest(userId, new ReviewContent("Updated content"), new ReviewRating(4));

        Review review = mock(Review.class);
        User user = mock(User.class);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        reviewService.updateReview(reviewId, request, userId);

        verify(review).setContent(any(ReviewContent.class));
        verify(review).setRating(any(ReviewRating.class));
    }

    @Test
    @DisplayName("리뷰 수정 사용자에게 권한이 없습니다")
    void testUpdateReviewNoPermission() {
        Long reviewId = 1L;
        Long userId = 1L;
        Long otherUserId = 2L;
        AddReviewRequest request = new AddReviewRequest(userId, new ReviewContent("Updated content"), new ReviewRating(4));

        Review review = mock(Review.class);
        User user = mock(User.class);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(otherUserId);

        assertThrows(NotHavePermissionReviewException.class, () -> reviewService.updateReview(reviewId, request, userId));
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void testDeleteReviewSuccess() {
        Long reviewId = 1L;
        Long userId = 1L;

        Review review = mock(Review.class);
        User user = mock(User.class);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        reviewService.deleteReview(reviewId, userId);

        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("리뷰 삭제 권한이 없습니다")
    void testDeleteReviewNoPermission() {
        Long reviewId = 1L;
        Long userId = 1L;
        Long otherUserId = 2L;

        Review review = mock(Review.class);
        User user = mock(User.class);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(review.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(otherUserId);

        assertThrows(NotHavePermissionReviewException.class, () -> reviewService.deleteReview(reviewId, userId));
    }

    @Test
    @DisplayName("관리자 소프트 삭제 성공 테스트")
    public void disableReviewByAdmin_Success() {
        Long reviewId = 1L;
        Review review = mock(Review.class);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.disableReviewByAdmin(reviewId);

        verify(review, times(1)).setEnabled(false);  // 리뷰의 enabled 필드를 false로 설정했는지 확인
        verify(reviewRepository, times(1)).save(review);  // 변경 사항이 저장되었는지 확인
    }
}
