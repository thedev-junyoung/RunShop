package com.example.runshop.controller;

import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    // MockMvc는 Spring MVC 컨트롤러의 동작을 테스트하기 위한 객체.
    // 실제 서버를 구동하지 않고도 컨트롤러의 요청과 응답을 테스트할 수 있도록 도와줌
    // HTTP 요청을 시뮬레이션하고, 응답의 상태 코드, 본문, 헤더 등을 검증 가능
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    // Spring의 웹 애플리케이션 컨텍스트.
    // MockMvc를 WebApplicationContext와 함께 설정하면, 실제 애플리케이션과 동일한 컨텍스트에서
    // 컨트롤러를 테스트할 수 있음.
    // 즉, 전체 Spring 애플리케이션 컨텍스트를 로드하여 컨트롤러, 서비스 등의 빈을 사용할 수 있게 해줌
    private final WebApplicationContext webApplicationContext;

    public UserControllerTest(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // MockMvc를 WebApplicationContext를 이용해 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void 사용자정보_조회() throws Exception {
        // Given
        Long userId = 1L;
        UserDTO userDTO = UserDTO.builder()
                .id(userId)
                .email("testuser@example.com")
                .name("홍길동")
                .phone("010-1234-5678")
                .address("서울특별시 중구")
                .createdAt("2023-09-01T12:00:00")
                .enabled(true)
                .updatedAt("2023-09-01T12:00:00")
                .build();
        when(userService.getUserById(userId)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.phone").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.address").value("서울특별시 중구"))
                .andExpect(jsonPath("$.data.createdAt").value("2023-09-01T12:00:00"))
                .andExpect(jsonPath("$.data.enabled").value(true));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void 모든사용자_조회() throws Exception {
        // Given
        UserDTO user1 = UserDTO.builder()
                .id(1L)
                .email("user1@example.com")
                .name("testuser1")
                .phone("010-1234-5678")
                .address("부산광역시 해운대구")
                .createdAt("2023-09-01T12:00:00")
                .enabled(true)
                .updatedAt("2023-09-01T12:00:00")
                .build();

        UserDTO user2 = UserDTO.builder()
                .id(2L)
                .email("user2@example.com")
                .name("testuser2")
                .phone("010-5678-1234")
                .address("전라북도 남원시")
                .createdAt("2023-09-02T12:00:00")
                .enabled(true)
                .updatedAt("2023-09-02T12:00:00")
                .build();

        List<UserDTO> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모든 사용자 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("testuser1"))
                .andExpect(jsonPath("$.data[0].address").value("부산광역시 해운대구"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].name").value("testuser2"))
                .andExpect(jsonPath("$.data[1].address").value("전라북도 남원시"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void 사용자정보_수정() throws Exception {
        // Given
        Long userId = 1L;
        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"변경된 홍길동\", \"phone\":\"010-9876-5432\", \"address\":\"인천광역시 미추홀구\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보가 성공적으로 업데이트되었습니다."));

        verify(userService, times(1)).updateUserDetails(eq(userId), any(UpdateUserRequest.class));
    }
    @Test
    void 비밀번호_변경() throws Exception {
        // Given
        Long userId = 1L;
        // When & Then
        mockMvc.perform(patch("/api/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldPassword\", \"newPassword\":\"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호가 성공적으로 변경되었습니다."));

        verify(userService, times(1)).updatePassword(eq(userId), any(UpdatePasswordRequest.class));
    }

    @Test
    void 사용자_비활성화() throws Exception {
        // Given
        Long userId = 1L;
        // When & Then
        mockMvc.perform(patch("/api/users/{id}/disabled", userId))
                .andDo(print())  // 응답 및 로그 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 계정이 성공적으로 비활성화되었습니다."));

        verify(userService, times(1)).disabled(userId);
    }
}
