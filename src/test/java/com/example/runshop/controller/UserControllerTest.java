package com.example.runshop.controller;

import com.example.runshop.model.dto.user.UpdatePasswordRequest;
import com.example.runshop.model.dto.user.UpdateRoleRequest;
import com.example.runshop.model.dto.user.UpdateUserRequest;
import com.example.runshop.model.dto.user.UserDTO;
import com.example.runshop.model.enums.UserRole;
import com.example.runshop.model.vo.user.Address;
import com.example.runshop.model.vo.user.Email;
import com.example.runshop.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUser;
    private final WebApplicationContext webApplicationContext;

    public UserControllerTest(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testUser = UserDTO.builder()
                .id(1L)
                .email(new Email("testuser@example.com"))
                .name("Test User")
                .phone("010-1234-5678")
                .address(new Address("Test Street", "101", "Test City", "Test Region", "12345"))
                .createdAt("2023-09-01T12:00:00")
                .enabled(true)
                .updatedAt("2023-09-01T12:00:00")
                .build();
    }

    @Test
    @DisplayName("사용자 정보 조회")
    void SelectUser() throws Exception {
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
    @DisplayName("모든 사용자 정보 페이징 조회")
    void selectAllUsersWithPaging() throws Exception {
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
        Page<UserDTO> pagedUsers = new PageImpl<>(users); // 페이징된 결과 생성
        Pageable pageable = PageRequest.of(0, 10); // 0번째 페이지, 10개의 항목

        // 페이징된 결과를 반환하도록 설정
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(pagedUsers);

        // When & Then
        mockMvc.perform(get("/api/users")
                        .param("page", "0") // 페이지 번호
                        .param("size", "10")) // 페이지당 항목 수
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모든 사용자 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].name").value("testuser1"))
                .andExpect(jsonPath("$.data.content[0].address.street").value("부산광역시 해운대구")) // Address VO 적용
                .andExpect(jsonPath("$.data.content[0].address.detailedAddress").value("102호"))
                .andExpect(jsonPath("$.data.content[0].address.city").value("해운대구"))
                .andExpect(jsonPath("$.data.content[0].address.region").value("부산"))
                .andExpect(jsonPath("$.data.content[0].address.zipCode").value("67890"))
                .andExpect(jsonPath("$.data.content[1].id").value(2L))
                .andExpect(jsonPath("$.data.content[1].name").value("testuser2"))
                .andExpect(jsonPath("$.data.content[1].address.street").value("전라북도 남원시")) // Address VO 적용
                .andExpect(jsonPath("$.data.content[1].address.detailedAddress").value("103호"))
                .andExpect(jsonPath("$.data.content[1].address.city").value("남원시"))
                .andExpect(jsonPath("$.data.content[1].address.region").value("전라북도"))
                .andExpect(jsonPath("$.data.content[1].address.zipCode").value("12345"));

        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }


    @Test
    @DisplayName("사용자정보_수정")
    void UpdateUser() throws Exception {
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
    @DisplayName("비밀번호 변경")
    void UpdatePassword() throws Exception {
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
    @DisplayName("사용자_비활성화")
    void DisabledUser() throws Exception {
        // Given
        Long userId = 1L;
        // When & Then
        mockMvc.perform(delete("/api/users/{id}/disabled", userId))
                .andDo(print())  // 응답 및 로그 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 계정이 성공적으로 비활성화되었습니다."));

        verify(userService, times(1)).disabled(userId);
    }
    @Test
    @DisplayName("관리자가 사용자 정보 조회")
    @WithMockUser(roles = "ADMIN")
    void getUser_AsAdmin() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.id").value(1L));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("일반 사용자가 자신의 정보 조회")
    void getUser_AsSelf() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1").with(user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.id").value(1L));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("관리자가 모든 사용자 정보 조회")
    @WithMockUser(roles = "ADMIN")
    void getAllUsers() throws Exception {
        Page<UserDTO> userPage = new PageImpl<>(Arrays.asList(testUser));
        when(userService.getAllUsers(any())).thenReturn(userPage);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모든 사용자 정보를 성공적으로 조회했습니다."))
                .andExpect(jsonPath("$.data.content[0].id").value(1L));

        verify(userService, times(1)).getAllUsers(any());
    }

    @Test
    @DisplayName("관리자가 사용자 정보 수정")
    @WithMockUser(roles = "ADMIN")
    void updateUser_AsAdmin() throws Exception {
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("Updated Name")
                .phone("010-9876-5432")
                .address(new Address("New Street", "102", "New City", "New Region", "54321"))
                .build();
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보가 성공적으로 업데이트되었습니다."));

        verify(userService, times(1)).updateUserDetails(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("일반 사용자가 자신의 정보 수정")
    void updateUser_AsSelf() throws Exception {
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("Updated Name")
                .phone("010-9876-5432")
                .address(new Address("New Street", "102", "New City", "New Region", "54321"))
                .build();
        mockMvc.perform(put("/api/users/1")
                        .with(user("1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 정보가 성공적으로 업데이트되었습니다."));

        verify(userService, times(1)).updateUserDetails(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("일반 사용자가 비밀번호 변경")
    void updatePassword() throws Exception {
        UpdatePasswordRequest passwordRequest = new UpdatePasswordRequest("oldPassword", "newPassword");

        mockMvc.perform(patch("/api/users/1/password")
                        .with(user("1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호가 성공적으로 변경되었습니다."));

        verify(userService, times(1)).updatePassword(eq(1L), any(UpdatePasswordRequest.class));
    }

    @Test
    @DisplayName("관리자가 사용자 권한 변경")
    @WithMockUser(roles = "ADMIN")
    void updateRole() throws Exception {
        UpdateRoleRequest roleRequest = new UpdateRoleRequest(UserRole.SELLER);

        mockMvc.perform(patch("/api/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 권한이 성공적으로 변경되었습니다."));

        verify(userService, times(1)).updateUserRole(eq(1L), any(UpdateRoleRequest.class));
    }

    @Test
    @DisplayName("관리자가 사용자 계정 비활성화")
    @WithMockUser(roles = "ADMIN")
    void deactivateUser_AsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/1/disabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 계정이 성공적으로 비활성화되었습니다."));

        verify(userService, times(1)).disabled(1L);
    }

    @Test
    @DisplayName("일반 사용자가 자신의 계정 비활성화")
    void deactivateUser_AsSelf() throws Exception {
        mockMvc.perform(delete("/api/users/1/disabled").with(user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 계정이 성공적으로 비활성화되었습니다."));

        verify(userService, times(1)).disabled(1L);
    }
}
