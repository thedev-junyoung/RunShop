package com.example.runshop.model.dto.review;

import com.example.runshop.model.vo.review.ReviewContent;
import com.example.runshop.model.vo.review.ReviewRating;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddReviewRequest {
    private Long userId;     // 리뷰 작성자 ID
    private ReviewContent content;  // 리뷰 내용
    private ReviewRating rating;    // 리뷰 평점 (1~5)
}
