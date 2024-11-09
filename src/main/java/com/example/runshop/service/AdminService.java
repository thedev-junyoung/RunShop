package com.example.runshop.service;

import com.example.runshop.exception.review.ReviewNotFoundException;
import com.example.runshop.exception.user.UserNotFoundException;
import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.entity.Review;
import com.example.runshop.repository.UserRepository;
import com.example.runshop.repository.ReviewRepository;
import com.example.runshop.utils.mapper.ReviewMapper;
import com.example.runshop.utils.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional(readOnly = true)
public class AdminService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;

    public AdminService(UserRepository userRepository, ReviewRepository reviewRepository, UserMapper userMapper, ReviewMapper reviewMapper) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.userMapper = userMapper;
        this.reviewMapper = reviewMapper;
    }

    @Transactional
    public void approveSeller(Long sellerId) {
        User seller = findUserOrThrow(sellerId);
        seller.setApproved(true);
        userRepository.save(seller);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = findReviewOrThrow(reviewId);
        review.setEnabled(false);
//        reviewRepository.delete(review);
    }

    public UserDTO getUser(Long userId) {
        User user = findUserOrThrow(userId);
        return userMapper.userToUserDTO(user);
    }

    @Transactional
    public void manageUserStatus(Long userId, boolean enabled) {
        User user = findUserOrThrow(userId);
        user.updateEnabledAccount(enabled);
        userRepository.save(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 회원을 찾을 수 없습니다."));
    }

    private Review findReviewOrThrow(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰를 찾을 수 없습니다."));
    }

    public Page<ReviewDTO> getReportedReviews(Pageable pageable) {
        return reviewRepository.findByReportedTrue(pageable).map(reviewMapper::reviewToReviewDTO);
    }

}
