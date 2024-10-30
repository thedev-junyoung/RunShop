package com.example.runshop.repository;

import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.model.entity.Review;
import com.example.runshop.utils.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    Page<Review> findByReportedTrue(Pageable pageable); // 수정된 부분
}
