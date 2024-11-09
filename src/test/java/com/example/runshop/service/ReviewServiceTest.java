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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private Review review; // Review를 Mock 객체로 추가
    @InjectMocks
    private ReviewService reviewService;

    private Product product;
    private User user;
    private AddReviewRequest addReviewRequest;

    @BeforeEach
    void setUp() {
        product = new Product();
        user = new User();
        addReviewRequest = AddReviewRequest.builder()
                .userId(1L)
                .content(new ReviewContent("Great product!"))
                .rating(new ReviewRating(5))
                .build();
    }

    @Test
    @DisplayName("리뷰 추가 성공")
    void addReview_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        reviewService.addReview(1L, addReviewRequest);

        verify(productRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 추가 시 상품을 찾을 수 없는 경우 예외 발생")
    void addReview_ProductNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> reviewService.addReview(1L, addReviewRequest));

        verify(productRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 추가 시 사용자를 찾을 수 없는 경우 예외 발생")
    void addReview_UserNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.addReview(1L, addReviewRequest));

        verify(productRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("상품 ID로 리뷰 목록 조회 성공")
    void getReviewsByProductId_Success() {
        when(reviewRepository.findByProductId(anyLong())).thenReturn(List.of(review));
        when(reviewMapper.riciewToReviewDTOList(anyList())).thenReturn(List.of(new ReviewDTO()));

        List<ReviewDTO> result = reviewService.getReviewsByProductId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByProductId(anyLong());
    }

    @Test
    @DisplayName("리뷰 ID로 리뷰 조회 성공")
    void getReviewById_Success() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewMapper.reviewToReviewDTO(any(Review.class))).thenReturn(new ReviewDTO());

        ReviewDTO result = reviewService.getReviewById(1L);

        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(anyLong());
        verify(reviewMapper, times(1)).reviewToReviewDTO(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 조회 시 리뷰를 찾을 수 없는 경우 예외 발생")
    void getReviewById_ReviewNotFoundException() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(1L));

        verify(reviewRepository, times(1)).findById(anyLong());
        verify(reviewMapper, never()).reviewToReviewDTO(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_Success() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        doNothing().when(review).verifyUserPermission(anyLong());

        reviewService.updateReview(1L, addReviewRequest, 1L);

        verify(reviewRepository, times(1)).findById(anyLong());
        verify(review, times(1)).verifyUserPermission(anyLong());
    }

    @Test
    @DisplayName("리뷰 수정 시 리뷰를 찾을 수 없는 경우 예외 발생")
    void updateReview_ReviewNotFoundException() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(1L, addReviewRequest, 1L));

        verify(reviewRepository, times(1)).findById(anyLong());
        verify(review, never()).verifyUserPermission(anyLong());
    }

    @Test
    @DisplayName("리뷰 수정 시 권한이 없는 경우 예외 발생")
    void updateReview_NotHavePermissionReviewException() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        doThrow(new NotHavePermissionReviewException("No permission")).when(review).verifyUserPermission(anyLong());

        assertThrows(NotHavePermissionReviewException.class, () -> reviewService.updateReview(1L, addReviewRequest, 1L));

        verify(reviewRepository, times(1)).findById(anyLong());
        verify(review, times(1)).verifyUserPermission(anyLong());
    }

    @Test
    @DisplayName("신고된 리뷰 비활성화 성공")
    void disableReportedReview_Success() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        reviewService.disableReportedReview(1L);

        verify(reviewRepository, times(1)).findById(anyLong());
        verify(review, times(1)).disable();
    }

    @Test
    @DisplayName("신고된 리뷰 비활성화 시 리뷰를 찾을 수 없는 경우 예외 발생")
    void disableReportedReview_ReviewNotFoundException() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.disableReportedReview(1L));

        verify(reviewRepository, times(1)).findById(anyLong());
        verify(review, never()).disable();
    }
}
