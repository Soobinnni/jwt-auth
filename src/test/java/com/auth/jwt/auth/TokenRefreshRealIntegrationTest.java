package com.auth.jwt.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth.jwt.auth.presentation.dto.request.LoginRequest;
import com.auth.jwt.auth.presentation.dto.request.TokenReissueRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@DisplayName("[TokenRefreshRealIntegrationTest] 토큰 갱신 실제 통합 테스트")
class TokenRefreshRealIntegrationTest {

  private static final String LOGIN_URL = "/login";
  private static final String REFRESH_URL = "/refresh-token";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("토큰 갱신 전체 플로우 테스트 - 로그인 → 토큰 갱신")
  void should_RefreshTokensSuccessfully_When_FullFlowExecuted() throws Exception {
    // 1. 먼저 로그인하여 실제 토큰 획득
    LoginRequest loginRequest = new LoginRequest("user", "user1234");

    MvcResult loginResult =
        mockMvc
            .perform(
                post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

    // 2. 로그인 응답에서 토큰 추출
    String loginResponse = loginResult.getResponse().getContentAsString();
    JsonNode loginJson = objectMapper.readTree(loginResponse);
    String originalAccessToken = loginJson.get("accessToken").asText();
    String originalRefreshToken = loginJson.get("refreshToken").asText();

    // 3. 토큰 갱신 요청
    TokenReissueRequest refreshRequest =
        new TokenReissueRequest(originalAccessToken, originalRefreshToken);

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists())
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.refreshToken").isString())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.refreshToken").isNotEmpty())
        // 토큰이 존재하고 올바른 형식인지만 확인 (같은 시간에 생성되면 동일할 수 있음)
        .andExpect(
            jsonPath("$.accessToken")
                .value(
                    org.hamcrest.Matchers.matchesPattern(
                        "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$")));
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 존재하지 않는 리프레시 토큰")
  void should_FailRefresh_When_RefreshTokenNotExists() throws Exception {
    // given
    String fakeAccessToken =
        "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiI5OTkiLCJhdXRoIjoiUk9MRV9VU0VSIiwidXNlcm5hbWUiOiJmYWtldXNlciIsInR5cGUiOiJhY2Nlc3MifQ.fake_signature";
    String fakeRefreshToken =
        "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiI5OTkiLCJ0eXBlIjoicmVmcmVzaCIsInVzZXJuYW1lIjoiZmFrZXVzZXIifQ.fake_signature";

    TokenReissueRequest request = new TokenReissueRequest(fakeAccessToken, fakeRefreshToken);

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").exists());
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 잘못된 JSON 형식")
  void should_FailRefresh_When_JsonIsInvalid() throws Exception {
    // given
    String invalidJson = "{ invalid json }";

    // when & then
    mockMvc
        .perform(post(REFRESH_URL).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("TOKEN_REISSUE_FAILED"));
  }

  @Test
  @DisplayName("JWT 토큰 형식 검증 - 갱신된 토큰이 올바른 JWT 형식")
  void should_ReturnValidJwtFormat_When_RefreshSuccessful() throws Exception {
    // 1. 로그인하여 토큰 획득
    LoginRequest loginRequest = new LoginRequest("admin", "admin123");

    MvcResult loginResult =
        mockMvc
            .perform(
                post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

    String loginResponse = loginResult.getResponse().getContentAsString();
    JsonNode loginJson = objectMapper.readTree(loginResponse);
    String accessToken = loginJson.get("accessToken").asText();
    String refreshToken = loginJson.get("refreshToken").asText();

    // 2. 토큰 갱신 및 JWT 형식 검증
    TokenReissueRequest refreshRequest = new TokenReissueRequest(accessToken, refreshToken);

    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.accessToken")
                .value(
                    org.hamcrest.Matchers.matchesPattern(
                        "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$")))
        .andExpect(
            jsonPath("$.refreshToken")
                .value(
                    org.hamcrest.Matchers.matchesPattern(
                        "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$")));
  }

  @Test
  @DisplayName("다중 토큰 갱신 테스트 - 갱신된 토큰으로 다시 갱신")
  void should_AllowMultipleRefresh_When_TokensAreValid() throws Exception {
    // 1. 로그인
    LoginRequest loginRequest = new LoginRequest("user", "user1234");
    MvcResult loginResult =
        mockMvc
            .perform(
                post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
    String accessToken = loginJson.get("accessToken").asText();
    String refreshToken = loginJson.get("refreshToken").asText();

    // 2. 첫 번째 토큰 갱신
    TokenReissueRequest firstRefresh = new TokenReissueRequest(accessToken, refreshToken);
    MvcResult firstRefreshResult =
        mockMvc
            .perform(
                post(REFRESH_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(firstRefresh)))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode firstRefreshJson =
        objectMapper.readTree(firstRefreshResult.getResponse().getContentAsString());
    String secondAccessToken = firstRefreshJson.get("accessToken").asText();
    String secondRefreshToken = firstRefreshJson.get("refreshToken").asText();

    // 3. 두 번째 토큰 갱신 (갱신된 토큰으로 다시 갱신)
    TokenReissueRequest secondRefresh =
        new TokenReissueRequest(secondAccessToken, secondRefreshToken);
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRefresh)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }
}
