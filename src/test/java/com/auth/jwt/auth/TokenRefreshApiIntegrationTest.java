package com.auth.jwt.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth.jwt.auth.application.AuthenticationService;
import com.auth.jwt.auth.application.dto.command.TokenReissueCommand;
import com.auth.jwt.auth.application.dto.result.CreateTokenPairResult;
import com.auth.jwt.auth.application.dto.result.TokenReissueResult;
import com.auth.jwt.auth.presentation.dto.request.TokenReissueRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("[TokenRefreshApiIntegrationTest] 토큰 갱신 API 테스트")
class TokenRefreshApiIntegrationTest {

  private static final String REFRESH_URL = "/refresh-token";
  private static final String VALID_ACCESS_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNjk5OTk5OTk5LCJleHAiOjE2OTk5OTk5OTl9.sample_signature";
  private static final String VALID_REFRESH_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwiaWF0IjoxNjk5OTk5OTk5LCJleHAiOjE2OTk5OTk5OTl9.sample_signature";
  private static final String NEW_ACCESS_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMDAzMDB9.new_signature";
  private static final String NEW_REFRESH_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMDAzMDB9.new_signature";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private AuthenticationService authenticationService;

  private void mockSuccessfulTokenRefresh(Long userId) {
    CreateTokenPairResult newTokenPair =
        new CreateTokenPairResult(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
    TokenReissueResult successResult = TokenReissueResult.success(userId, newTokenPair);

    given(authenticationService.reIssueTokenPair(any(TokenReissueCommand.class)))
        .willReturn(successResult);
  }

  private void mockFailedTokenRefresh(String code, String message) {
    TokenReissueResult failResult = TokenReissueResult.fail(code, message);

    given(authenticationService.reIssueTokenPair(any(TokenReissueCommand.class)))
        .willReturn(failResult);
  }

  @Test
  @DisplayName("토큰 갱신 성공 - 유효한 리프레시 토큰")
  void should_ReturnNewTokens_When_ValidRefreshTokenProvided() throws Exception {
    // given
    TokenReissueRequest request = new TokenReissueRequest(VALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN);
    mockSuccessfulTokenRefresh(1L);

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(NEW_ACCESS_TOKEN))
        .andExpect(jsonPath("$.refreshToken").value(NEW_REFRESH_TOKEN))
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.refreshToken").isNotEmpty());
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 유효하지 않은 리프레시 토큰")
  void should_ReturnUnauthorized_When_RefreshTokenIsInvalid() throws Exception {
    // given
    TokenReissueRequest request =
        new TokenReissueRequest(VALID_ACCESS_TOKEN, "invalid.refresh.token");
    mockFailedTokenRefresh("INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다.");

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"))
        .andExpect(jsonPath("$.error.message").value("유효하지 않은 리프레시 토큰입니다."));
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 만료된 리프레시 토큰")
  void should_ReturnUnauthorized_When_RefreshTokenIsExpired() throws Exception {
    // given
    TokenReissueRequest request =
        new TokenReissueRequest(VALID_ACCESS_TOKEN, "expired.refresh.token");
    mockFailedTokenRefresh("TOKEN_EXPIRED", "토큰이 만료되었습니다.");

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("TOKEN_EXPIRED"))
        .andExpect(jsonPath("$.error.message").value("토큰이 만료되었습니다."));
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 등록되지 않은 리프레시 토큰")
  void should_ReturnUnauthorized_When_RefreshTokenNotRegistered() throws Exception {
    // given
    TokenReissueRequest request =
        new TokenReissueRequest(VALID_ACCESS_TOKEN, "unregistered.refresh.token");
    mockFailedTokenRefresh("INVALID_REFRESH_TOKEN", "등록되지 않은 리프레시 토큰입니다.");

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"))
        .andExpect(jsonPath("$.error.message").value("등록되지 않은 리프레시 토큰입니다."));
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 사용자를 찾을 수 없음")
  void should_ReturnUnauthorized_When_UserNotFound() throws Exception {
    // given
    TokenReissueRequest request = new TokenReissueRequest(VALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN);
    mockFailedTokenRefresh("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.error.message").value("사용자를 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 토큰에서 사용자 정보를 찾을 수 없음")
  void should_ReturnUnauthorized_When_TokenPayloadInvalid() throws Exception {
    // given
    TokenReissueRequest request =
        new TokenReissueRequest(VALID_ACCESS_TOKEN, "malformed.payload.token");
    mockFailedTokenRefresh("INVALID_TOKEN_PAYLOAD", "토큰에서 사용자 정보를 찾을 수 없습니다.");

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_TOKEN_PAYLOAD"))
        .andExpect(jsonPath("$.error.message").value("토큰에서 사용자 정보를 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 리프레시 토큰이 null")
  void should_ReturnUnauthorized_When_RefreshTokenIsNull() throws Exception {
    // given
    TokenReissueRequest request = new TokenReissueRequest(VALID_ACCESS_TOKEN, null);
    // RefreshTokenFilter에서 JSON 파싱 후 처리되므로 실제로는 AuthenticationService까지 전달됨
    mockFailedTokenRefresh("INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다.");

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 액세스 토큰이 null")
  void should_ReturnUnauthorized_When_AccessTokenIsNull() throws Exception {
    // given
    TokenReissueRequest request = new TokenReissueRequest(null, VALID_REFRESH_TOKEN);
    mockFailedTokenRefresh("INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다.");

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));
  }

  @Test
  @DisplayName("토큰 갱신 실패 - 잘못된 JSON 형식")
  void should_ReturnUnauthorized_When_JsonFormatIsInvalid() throws Exception {
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
  @DisplayName("JWT 토큰 형식 검증 - 갱신된 토큰이 올바른 JWT 형식인지 확인")
  void should_ReturnValidJwtTokens_When_RefreshSuccessful() throws Exception {
    // given
    TokenReissueRequest request = new TokenReissueRequest(VALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN);
    mockSuccessfulTokenRefresh(1L);

    // when & then
    mockMvc
        .perform(
            post(REFRESH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
}
