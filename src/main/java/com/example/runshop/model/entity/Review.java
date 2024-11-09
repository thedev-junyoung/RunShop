package com.example.runshop.model.entity;

import com.example.runshop.exception.review.NotHavePermissionReviewException;
import com.example.runshop.model.vo.review.ReviewContent;
import com.example.runshop.model.vo.review.ReviewRating;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "review")
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "content"))
    private ReviewContent content;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "rating"))
    private ReviewRating rating; // VO로 평점 관리

    @Column(nullable = false)
    private boolean enabled = true; // 기본값을 true로 설정하여 활성화된 상태로 시작

    @Column(nullable = false)
    private boolean reported = false; // 기본값을 false로 설정하여 신고되지 않은 상태로 시작


    public static Review createReview(Product product, User user, ReviewContent content, ReviewRating rating) {
        return Review.builder()
                .product(product)
                .user(user)
                .rating(rating)
                .content(content)
                .build();
    }

    // 리뷰 내용 수정
    public void updateReviewContent(ReviewContent newContent, ReviewRating newRating) {
        if (newContent != null) this.content = newContent;
        if (newRating != null) this.rating = newRating;
    }

    // 리뷰 작성자 권한 확인
    public void verifyUserPermission(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new NotHavePermissionReviewException("해당 리뷰에 대한 수정 권한이 없습니다.");
        }
    }

    // 리뷰 비활성화
    public void disable() {
        this.enabled = false;
    }

    // 리뷰 신고 처리
    public void report() {
        this.reported = true;
    }
}
