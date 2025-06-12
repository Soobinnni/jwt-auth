package com.auth.jwt.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth.jwt.auth.application.AuthorizationService;
import com.auth.jwt.auth.application.dto.result.TokenValidationResult;
import com.auth.jwt.common.model.CustomPrincipal;
import com.auth.jwt.user.application.UserCommandService;
import com.auth.jwt.user.application.dto.command.RoleGrantCommand;
import com.auth.jwt.user.application.exception.UserNotFoundException;
import com.auth.jwt.user.domain.entity.Role;
import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.vo.Nickname;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("[AdminUserControllerJwtTest] 관리자 권한 부여 테스트 - JWT 토큰 기반")
public class AdminUserApiIntegrationTest {

  private static final String VALID_ADMIN_TOKEN = "Bearer valid.admin.token";
  private static final String VALID_USER_TOKEN = "Bearer valid.user.token";
  private static final String INVALID_TOKEN = "Bearer invalid.token";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserCommandService userCommandService;
  @MockitoBean private AuthorizationService authorizationService;

  private void mockAdminToken(Long userId) {
    given(authorizationService.validateAccessToken(anyString()))
        .willReturn(TokenValidationResult.success(userId));
    given(authorizationService.getPrincipal(userId))
        .willReturn(new CustomPrincipal(userId, "ROLE_ADMIN"));
  }

  private void mockUserToken(Long userId) {
    given(authorizationService.validateAccessToken(anyString()))
        .willReturn(TokenValidationResult.success(userId));
    given(authorizationService.getPrincipal(userId))
        .willReturn(new CustomPrincipal(userId, "ROLE_USER"));
  }

  private void mockInvalidToken(String code, String message) {
    given(authorizationService.validateAccessToken(anyString()))
        .willReturn(TokenValidationResult.fail(code, message));
  }

  @Test
  @DisplayName("관리자 권한 부여 성공 - 유효한 관리자 토큰")
  void should_GrantAdminRole_When_ValidAdminTokenProvided() throws Exception {
    Long adminUserId = 1L;
    Long targetUserId = 2L;

    mockAdminToken(adminUserId);

    User mockUser =
        new User(
            UserId.of(targetUserId),
            Username.of("targetuser"),
            null,
            Nickname.of("TargetUser"),
            Role.ADMIN);

    given(userCommandService.grantAdminRole(any(RoleGrantCommand.class))).willReturn(mockUser);

    mockMvc
        .perform(
            patch("/admin/users/{userId}/roles", targetUserId)
                .header("Authorization", VALID_ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(targetUserId))
        .andExpect(jsonPath("$.username").value("targetuser"))
        .andExpect(jsonPath("$.nickname").value("TargetUser"))
        .andExpect(jsonPath("$.role").value("관리자"));
  }

  @Test
  @DisplayName("관리자 권한 부여 실패 - 일반 사용자 토큰")
  void should_RejectAdminGrant_When_UserRoleTriesToGrant() throws Exception {
    Long userId = 2L;
    Long targetUserId = 3L;

    mockUserToken(userId);

    mockMvc
        .perform(
            patch("/admin/users/{userId}/roles", targetUserId)
                .header("Authorization", VALID_USER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("ACCESS_DENIED"));
  }

  @Test
  @DisplayName("관리자 권한 부여 실패 - 유효하지 않은 토큰")
  void should_RejectAdminGrant_When_InvalidTokenProvided() throws Exception {
    Long targetUserId = 2L;

    mockInvalidToken("INVALID_TOKEN", "유효하지 않은 토큰입니다.");

    mockMvc
        .perform(
            patch("/admin/users/{userId}/roles", targetUserId)
                .header("Authorization", INVALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"))
        .andExpect(jsonPath("$.error.message").value("유효하지 않은 토큰입니다."));
  }

  @Test
  @DisplayName("관리자 권한 부여 실패 - 토큰 없이 접근")
  void should_RejectAdminGrant_When_TokenMissing() throws Exception {
    Long targetUserId = 2L;

    mockMvc
        .perform(
            patch("/admin/users/{userId}/roles", targetUserId)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("TOKEN_INVALID"));
  }

  @Test
  @DisplayName("관리자 권한 부여 실패 - 존재하지 않는 사용자")
  void should_ReturnNotFound_When_UserDoesNotExist() throws Exception {
    Long adminUserId = 1L;
    Long nonExistentUserId = 999L;

    mockAdminToken(adminUserId);

    willThrow(new UserNotFoundException()).given(userCommandService).grantAdminRole(any());

    mockMvc
        .perform(
            patch("/admin/users/{userId}/roles", nonExistentUserId)
                .header("Authorization", VALID_ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND_USER"))
        .andExpect(jsonPath("$.error.message").value("사용자를 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("관리자 권한 부여 실패 - 만료된 토큰")
  void should_RejectAdminGrant_When_TokenExpired() throws Exception {
    Long targetUserId = 2L;

    mockInvalidToken("TOKEN_EXPIRED", "토큰이 만료되었습니다.");

    mockMvc
        .perform(
            patch("/admin/users/{userId}/roles", targetUserId)
                .header("Authorization", "Bearer expired.token")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("TOKEN_EXPIRED"))
        .andExpect(jsonPath("$.error.message").value("토큰이 만료되었습니다."));
  }

  @Test
  @DisplayName("관리자 권한 부여 실패 - Authorization 헤더 형식이 잘못됨")
  void should_RejectAdminGrant_When_AuthorizationHeaderMalformed() throws Exception {
    Long targetUserId = 2L;

    mockMvc
        .perform(
            patch("/admin/users/{userId}/roles", targetUserId)
                .header("Authorization", "invalid-header")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("TOKEN_INVALID"));
  }
}
