package com.example.runshop.model.vo.review;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;
import com.example.runshop.exception.review.InvalidRatingException;

@Embeddable
public record ReviewRating(int value) {

    @JsonCreator
    public ReviewRating {
        if (value < 1 || value > 5) {
            throw new InvalidRatingException("평점은 1부터 5 사이여야 합니다.");
        }
    }

    @JsonValue
    @Override
    public int value() {
        return value;
    }
}
