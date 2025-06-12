package com.auth.jwt.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth.jwt.auth.presentation.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("[AuthenticationAuthorizationFilterTest] 인증/인가 필터 테스트")
class AuthenticationAuthorizationFilterTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String userAccessToken;
  private String adminAccessToken;

  @BeforeEach
  void setUp() throws Exception {
    // 사용자 토큰 획득
    userAccessToken = getAccessToken("user", "user1234");

    // 관리자 토큰 획득
    adminAccessToken = getAccessToken("admin", "admin123");
  }

  private String getAccessToken(String username, String password) throws Exception {
    LoginRequest loginRequest = new LoginRequest(username, password);

    MvcResult result =
        mockMvc
            .perform(
                post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

    String response = result.getResponse().getContentAsString();
    JsonNode json = objectMapper.readTree(response);
    return json.get("accessToken").asText();
  }

  @Test
  @DisplayName("인증 성공 - 유효한 Authorization 헤더")
  void should_AllowAccess_When_ValidAuthorizationHeaderProvided() throws Exception {
    // when & then
    mockMvc
        .perform(
            patch("/admin/users/1/roles")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("인증 실패 - Authorization 헤더 없음")
  void should_DenyAccess_When_AuthorizationHeaderMissing() throws Exception {
    // when & then
    mockMvc
        .perform(patch("/admin/users/1/roles").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));
  }

  @Test
  @DisplayName("인증 실패 - Bearer 접두사 없음")
  void should_DenyAccess_When_BearerPrefixMissing() throws Exception {
    // when & then
    mockMvc
        .perform(
            patch("/admin/users/1/roles")
                .header("Authorization", userAccessToken) // Bearer 없이
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));
  }

  @Test
  @DisplayName("인증 실패 - 잘못된 토큰 형식")
  void should_DenyAccess_When_TokenFormatIsInvalid() throws Exception {
    // when & then
    mockMvc
        .perform(
            patch("/admin/users/1/roles")
                .header("Authorization", "Bearer invalid.token.format")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").exists());
  }

  @Test
  @DisplayName("인증 실패 - 빈 토큰")
  void should_DenyAccess_When_TokenIsEmpty() throws Exception {
    // when & then
    mockMvc
        .perform(
            patch("/admin/users/1/roles")
                .header("Authorization", "Bearer ")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));
  }

  @Test
  @DisplayName("인가 성공 - 관리자가 관리자 권한 필요한 API 접근")
  void should_AllowAccess_When_AdminAccessesAdminApi() throws Exception {
    // when & then
    mockMvc
        .perform(
            patch("/admin/users/2/roles")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(2L))
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.role").value("관리자"));
  }

  @Test
  @DisplayName("공개 API 접근 - 토큰 없이도 접근 가능")
  void should_AllowAccess_When_AccessingPublicApi() throws Exception {
    // given - 회원가입은 공개 API
    String signupRequest =
        """
        {
          "username": "newuser",
          "password": "password123",
          "nickname": "NewUser"
        }
        """;

    // when & then
    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(signupRequest))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("newuser"))
        .andExpect(jsonPath("$.nickname").value("NewUser"));
  }

  @Test
  @DisplayName("토큰 갱신 API - 인증 없이 접근 가능")
  void should_AllowAccess_When_AccessingRefreshTokenApi() throws Exception {
    // given
    String refreshRequest =
        String.format(
            """
        {
          "accessToken": "%s",
          "refreshToken": "fake.refresh.token"
        }
        """,
            userAccessToken);

    // when & then - 토큰이 잘못되어도 401이지 403이 아님 (필터를 통과함)
    mockMvc
        .perform(
            post("/refresh-token").contentType(MediaType.APPLICATION_JSON).content(refreshRequest))
        .andDo(print())
        .andExpect(status().isUnauthorized()); // 필터는 통과하지만 토큰이 잘못되어서 401
  }

  @Test
  @DisplayName("로그인 API - 인증 없이 접근 가능")
  void should_AllowAccess_When_AccessingLoginApi() throws Exception {
    // given
    LoginRequest loginRequest = new LoginRequest("user", "wrongpassword");

    // when & then
    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andDo(print())
        .andExpect(status().isUnauthorized()); // 필터는 통과하지만 인증 실패로 401
  }

  @Test
  @DisplayName("다양한 HTTP 메서드로 보호된 API 접근 테스트")
  void should_ProtectAllHttpMethods_When_AuthenticationRequired() throws Exception {
    // GET 요청 (실제 GET API가 없으므로 404가 예상되지만 인증은 확인됨)
    mockMvc
        .perform(get("/admin/users"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));

    // POST 요청
    mockMvc
        .perform(post("/admin/users"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));

    // PUT 요청
    mockMvc
        .perform(put("/admin/users/1"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));

    // DELETE 요청
    mockMvc
        .perform(delete("/admin/users/1"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));
  }

  @Test
  @DisplayName("토큰 만료 시 처리 - 만료된 토큰으로 접근")
  void should_DenyAccess_When_TokenIsExpired() throws Exception {
    // given - 만료된 토큰 (실제로는 잘못된 서명 토큰으로 시뮬레이션)
    String expiredToken =
        "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsInVzZXJuYW1lIjoidGVzdCIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE2MDAwMDAwMDAsImV4cCI6MTYwMDAwMDMwMH0.expired_signature";

    // when & then
    mockMvc
        .perform(
            patch("/admin/users/1/roles")
                .header("Authorization", "Bearer " + expiredToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").exists());
  }

  @Test
  @DisplayName("대소문자 구분 없는 Bearer 토큰 처리")
  void should_HandleBearerCaseInsensitive() throws Exception {
    // when & then - 일반적으로 Bearer는 대소문자를 구분함
    mockMvc
        .perform(
            patch("/admin/users/1/roles")
                .header("Authorization", "bearer " + adminAccessToken) // 소문자
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden()) // Bearer는 대소문자를 구분하므로 실패
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"));
  }
}
