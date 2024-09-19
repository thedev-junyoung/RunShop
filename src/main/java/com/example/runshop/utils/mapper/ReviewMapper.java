package com.example.runshop.utils.mapper;

import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.model.entity.Review;
import com.example.runshop.model.vo.review.ReviewContent;
import com.example.runshop.model.vo.review.ReviewRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    // Review 엔티티를 ReviewDTO로 변환하는 매핑
    @Mapping(target = "content", source = "content")
    @Mapping(target = "rating", source = "rating")
    ReviewDTO reviewToReviewDTO(Review review);

    // Review 리스트를 ReviewDTO 리스트로 변환하는 매핑
    List<ReviewDTO> riciewToReviewDTOList(List<Review> reviews);

    // ReviewContent VO를 문자열로 변환
    default String map(ReviewContent reviewContent) {
        return reviewContent.value();
    }

    // ReviewRating VO를 정수형으로 변환
    default Integer map(ReviewRating reviewRating) {
        return reviewRating.value();
    }

    // LocalDateTime을 문자열로 변환하는 메소드
    @Named("mapDate")
    default String mapDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
