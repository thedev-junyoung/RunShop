package com.example.runshop.model.dto.review;

import com.example.runshop.model.vo.review.ReviewContent;
import com.example.runshop.model.vo.review.ReviewRating;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private Long id;
    private Long productId;
    private Long userId;
    private ReviewContent content;
    private ReviewRating rating;  // 1~5 사이의 평점
}
