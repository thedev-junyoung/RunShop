package com.example.runshop.controller;

import com.example.runshop.model.dto.review.AddReviewRequest;
import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.model.vo.review.ReviewContent;
import com.example.runshop.model.vo.review.ReviewRating;
import com.example.runshop.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {


    private MockMvc mockMvc;
    @MockBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReviewController(reviewService)).build();
    }
    @Test
    @DisplayName("리뷰 목록 조회 API 성공")
    public void GetReviewsByProductId_API_Success() throws Exception {
        Long productId = 1L;
        ReviewDTO review1 = ReviewDTO.builder()
                .userId(1L)
                .content(new ReviewContent("훌륭한 상품입니다."))
                .rating(new ReviewRating(5))
                .build();

        ReviewDTO review2 = ReviewDTO.builder()
                .userId(2L)
                .content(new ReviewContent("괜찮은 품질이네요."))
                .rating(new ReviewRating(4))
                .build();

        List<ReviewDTO> reviews = List.of(review1, review2);

        when(reviewService.getReviewsByProductId(productId)).thenReturn(reviews);

        mockMvc.perform(get("/api/products/{productId}/reviews", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].content").value("훌륭한 상품입니다."))
                .andExpect(jsonPath("$.data[0].rating").value(5))
                .andExpect(jsonPath("$.data[1].content").value("괜찮은 품질이네요."))
                .andExpect(jsonPath("$.data[1].rating").value(4));

        verify(reviewService, times(1)).getReviewsByProductId(productId);
    }

    @Test
    @DisplayName("리뷰 수정 API 성공")
    public void UpdateReview_API_Success() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(put("/api/products/reviews/{reviewId}", reviewId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1, \"content\":\"리뷰 내용 수정 테스트입니다\", \"rating\":4}")
                )
                .andExpect(status().isOk());

        verify(reviewService, times(1)).updateReview(eq(reviewId), any(AddReviewRequest.class), eq(userId));
    }

    @Test
    @DisplayName("리뷰 삭제 API 성공")
    public void DeleteReview_API_Success() throws Exception {
        Long reviewId = 1L;
        Long userId = 1L;

        mockMvc.perform(delete("/api/products/reviews/{reviewId}", reviewId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).deleteReview(eq(reviewId), eq(userId));
    }
}