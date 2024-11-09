package com.example.runshop.service;

import com.example.runshop.exception.review.ReviewNotFoundException;
import com.example.runshop.exception.user.UserNotFoundException;
import com.example.runshop.model.dto.review.ReviewDTO;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.model.entity.Review;
import com.example.runshop.model.entity.User;
import com.example.runshop.model.vo.user.Email;
import com.example.runshop.model.vo.user.Password;
import com.example.runshop.repository.ReviewRepository;
import com.example.runshop.repository.UserRepository;
import com.example.runshop.utils.mapper.ReviewMapper;
import com.example.runshop.utils.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserMapper userMapper;

    @Mock
    private ReviewMapper reviewMapper;
    @Test
    @DisplayName("판매자 승인 - 사용자 승인 상태가 true로 설정되는지 검증")
    public void approveSeller_ShouldSetApprovedToTrue() {
        // Given
        User testUser = new User();
        ReflectionTestUtils.setField(testUser, "email", new Email("test@gmail.com"));
        ReflectionTestUtils.setField(testUser, "password", new Password("test12341234"));
        testUser.setApproved(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        adminService.approveSeller(1L);

        // Then
        assertTrue(testUser.isApproved());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("판매자 승인 - 사용자를 찾을 수 없는 경우 예외 발생")
    public void approveSeller_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.approveSeller(1L));
    }

    @Test
    @DisplayName("리뷰 삭제 - 리뷰의 활성화 상태가 false로 설정되는지 검증")
    public void deleteReview_ShouldSetEnabledToFalse() {
        // Given
        Review testReview = new Review();
        testReview.setId(1L);
        testReview.setEnabled(true);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        adminService.deleteReview(1L);

        // Then
        assertFalse(testReview.isEnabled()); // 리뷰의 활성화 상태가 false로 설정되었는지 확인
        // 더티 체킹을 통한 자동 업데이트를 기대하므로 save 검증은 필요하지 않음
    }


    @Test
    @DisplayName("리뷰 삭제 - 리뷰를 찾을 수 없는 경우 예외 발생")
    public void deleteReview_ReviewNotFound_ShouldThrowException() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class, () -> adminService.deleteReview(1L));
    }

    @Test
    @DisplayName("사용자 조회 - UserDTO 반환 검증")
    public void getUser_ShouldReturnUserDTO() {
        // Given
        User testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        UserDTO userDTO = new UserDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.userToUserDTO(testUser)).thenReturn(userDTO);

        // When
        UserDTO result = adminService.getUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(userDTO, result);
    }
    @Test
    @DisplayName("사용자 조회 - 사용자를 찾을 수 없는 경우 예외 발생")
    public void getUser_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.getUser(1L));
    }

    @Test
    @DisplayName("사용자 상태 관리 - 사용자 활성화 상태 업데이트 검증")
    public void manageUserStatus_ShouldUpdateUserStatus() {
        // Given
        User testUser = new User();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testUser, "enabled",true);


        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        adminService.manageUserStatus(1L, false);

        // Then
        assertFalse(testUser.isEnabled());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("사용자 상태 관리 - 사용자를 찾을 수 없는 경우 예외 발생")
    public void manageUserStatus_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.manageUserStatus(1L, true));
    }

    @Test
    @DisplayName("신고된 리뷰 조회 - 페이징된 ReviewDTO 반환 검증")
    public void getReportedReviews_ShouldReturnPagedReviewDTO() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Review testReview = new Review();
        testReview.setId(1L);

        Page<Review> reviewPage = new PageImpl<>(List.of(testReview), pageable, 1);
        when(reviewRepository.findByReportedTrue(pageable)).thenReturn(reviewPage);

        ReviewDTO reviewDTO = new ReviewDTO();
        when(reviewMapper.reviewToReviewDTO(testReview)).thenReturn(reviewDTO);

        // When
        Page<ReviewDTO> result = adminService.getReportedReviews(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(reviewDTO, result.getContent().get(0));
    }
}
