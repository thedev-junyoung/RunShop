package com.example.runshop.controller;

import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateRoleRequest;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.model.vo.user.Address;
import com.example.runshop.model.vo.user.Email;
import com.example.runshop.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    @DisplayName("관리자 권한으로 특정 사용자 조회")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserAsAdmin() throws Exception {
        // Given
        Long userId = 1L;
        UserDTO userDTO = UserDTO.builder()
                .id(userId)
                .email(new Email("admin@example.com"))
                .name("관리자")
                .phone("010-1234-5678")
                .address(new Address("서울", "101호", "강남구", "서울특별시", "12345"))
                .enabled(true)
                .createdAt("2023-09-01T12:00:00")
                .updatedAt("2023-09-01T12:00:00")
                .build();

        when(userService.getUserById(userId)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value("admin@example.com"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("사용자 권한으로 자신의 정보 조회")
    @WithMockUser(username = "user1", roles = {"USER"})
    void getUserAsSelf() throws Exception {
        // Given
        Long userId = 1L;
        UserDTO userDTO = UserDTO.builder()
                .id(userId)
                .email(new Email("user1@example.com"))
                .name("사용자1")
                .phone("010-5678-1234")
                .address(new Address("서울", "102호", "강남구", "서울특별시", "67890"))
                .enabled(true)
                .createdAt("2023-09-01T12:00:00")
                .updatedAt("2023-09-01T12:00:00")
                .build();

        when(userService.getUserById(userId)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value("user1@example.com"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("사용자가 다른 사용자의 정보를 조회하려고 시도")
    @WithMockUser(username = "user2", roles = {"USER"})
    void getUserAsOtherUser() throws Exception {
        Long otherUserId = 1L;

        // 다른 사용자의 ID로 요청할 경우 접근 금지 상태 확인
        mockMvc.perform(get("/api/users/{id}", otherUserId))
                .andExpect(status().isForbidden()); // 접근 금지 상태 기대

        verify(userService, times(0)).getUserById(any(Long.class));
    }

    @Test
    @DisplayName("관리자 권한으로 사용자 권한 업데이트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserRoleAsAdmin() throws Exception {
        Long userId = 1L;
        mockMvc.perform(patch("/api/users/{id}/role", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"SELLER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 권한이 성공적으로 변경되었습니다."));

        verify(userService, times(1)).updateUserRole(eq(userId), any(UpdateRoleRequest.class));
    }

    @Test
    @DisplayName("일반 사용자가 다른 사용자의 비밀번호 변경 시도")
    @WithMockUser(username = "user1", roles = {"USER"})
    void updateOthersPasswordAsUser() throws Exception {
        Long otherUserId = 2L;
        mockMvc.perform(patch("/api/users/{id}/password", otherUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldPass\", \"newPassword\":\"newPass\"}"))
                .andExpect(status().isForbidden()); // 다른 사용자 비밀번호 변경 시도 시 403 기대

        verify(userService, times(0)).updatePassword(any(Long.class), any(UpdatePasswordRequest.class));
    }
}

