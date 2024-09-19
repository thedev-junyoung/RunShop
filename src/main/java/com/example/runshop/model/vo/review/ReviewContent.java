package com.example.runshop.model.vo.review;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;
import com.example.runshop.exception.review.InvalidReviewContentException;

@Embeddable
public record ReviewContent(String value) {

    @JsonCreator
    public ReviewContent {
        if (value == null || value.length() < 10 || value.length() > 1000) {
            throw new InvalidReviewContentException("리뷰 내용은 10자 이상, 1000자 이하이어야 합니다.");
        }
    }

    @JsonValue
    @Override
    public String value() {
        return value;
    }
}
