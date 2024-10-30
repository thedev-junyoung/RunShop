package com.example.runshop.model.entity;

import com.example.runshop.model.vo.review.ReviewContent;
import com.example.runshop.model.vo.review.ReviewRating;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}
