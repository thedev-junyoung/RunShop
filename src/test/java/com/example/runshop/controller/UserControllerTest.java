package com.example.runshop.controller;

import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.model.vo.Address;
import com.example.runshop.model.vo.Email;
import com.example.runshop.service.UserService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final WebApplicationContext webApplicationContext;

    public UserControllerTest(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void 사용자정보_조회() throws Exception {
        // Given
        Long userId = 1L;
        UserDTO userDTO = UserDTO.builder()
                .id(userId)
                .email(new Email("testuser@example.com")) // Email VO 적용
                .name("홍길동")
                .phone("010-1234-5678")
                .address(new Address("서울특별시 중구", "101호", "중구", "서울", "12345")) // Address VO 적용
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
                .andExpect(jsonPath("$.data.email").value("testuser@example.com")) // 수정된 부분: $.data.email
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.phone").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.address.street").value("서울특별시 중구")) // Address VO 적용
                .andExpect(jsonPath("$.data.address.detailedAddress").value("101호"))
                .andExpect(jsonPath("$.data.address.city").value("중구"))
                .andExpect(jsonPath("$.data.address.region").value("서울"))
                .andExpect(jsonPath("$.data.address.zipCode").value("12345"))
                .andExpect(jsonPath("$.data.createdAt").value("2023-09-01T12:00:00"))
                .andExpect(jsonPath("$.data.enabled").value(true));

        verify(userService, times(1)).getUserById(userId);
    }


    @Test
    void 모든사용자_조회() throws Exception {
        // Given
        UserDTO user1 = UserDTO.builder()
                .id(1L)
                .email(new Email("user1@example.com")) // Email VO 적용
                .name("testuser1")
                .phone("010-1234-5678")
                .address(new Address("부산광역시 해운대구", "102호", "해운대구", "부산", "67890")) // Address VO 적용
                .createdAt("2023-09-01T12:00:00")
                .enabled(true)
                .updatedAt("2023-09-01T12:00:00")
                .build();

        UserDTO user2 = UserDTO.builder()
                .id(2L)
                .email(new Email("user2@example.com")) // Email VO 적용
                .name("testuser2")
                .phone("010-5678-1234")
                .address(new Address("전라북도 남원시", "103호", "남원시", "전라북도", "12345")) // Address VO 적용
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
                .andExpect(jsonPath("$.data[0].address.street").value("부산광역시 해운대구")) // Address VO 적용
                .andExpect(jsonPath("$.data[0].address.detailedAddress").value("102호"))
                .andExpect(jsonPath("$.data[0].address.city").value("해운대구"))
                .andExpect(jsonPath("$.data[0].address.region").value("부산"))
                .andExpect(jsonPath("$.data[0].address.zipCode").value("67890"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].name").value("testuser2"))
                .andExpect(jsonPath("$.data[1].address.street").value("전라북도 남원시")) // Address VO 적용
                .andExpect(jsonPath("$.data[1].address.detailedAddress").value("103호"))
                .andExpect(jsonPath("$.data[1].address.city").value("남원시"))
                .andExpect(jsonPath("$.data[1].address.region").value("전라북도"))
                .andExpect(jsonPath("$.data[1].address.zipCode").value("12345"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void 사용자정보_수정() throws Exception {
        // Given
        Long userId = 1L;
        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"변경된 홍길동\", \"phone\":\"010-9876-5432\", \"address\":{\"street\":\"인천광역시 미추홀구\",\"detailedAddress\":\"104호\",\"city\":\"미추홀구\",\"region\":\"인천\",\"zipCode\":\"67890\"}}")) // Address VO 적용
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
