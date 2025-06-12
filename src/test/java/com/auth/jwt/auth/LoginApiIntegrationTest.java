package com.auth.jwt.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth.jwt.auth.application.AuthenticationService;
import com.auth.jwt.auth.application.dto.command.CredentialCommand;
import com.auth.jwt.auth.application.dto.command.SaveRefreshCommand;
import com.auth.jwt.auth.application.dto.result.AuthenticationResult;
import com.auth.jwt.auth.application.dto.result.CreateTokenPairResult;
import com.auth.jwt.auth.application.exception.AuthenticationException;
import com.auth.jwt.auth.presentation.dto.request.LoginRequest;
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
@DisplayName("[LoginApiIntegrationTest] 로그인 API 통합 테스트")
class LoginApiIntegrationTest {

  private static final String LOGIN_URL = "/login";
  private static final String VALID_USERNAME = "testuser";
  private static final String VALID_PASSWORD = "password123";
  private static final String INVALID_USERNAME = "wronguser";
  private static final String INVALID_PASSWORD = "wrongpass";
  private static final String SAMPLE_ACCESS_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNjk5OTk5OTk5LCJleHAiOjE2OTk5OTk5OTl9.sample_signature";
  private static final String SAMPLE_REFRESH_TOKEN =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwiaWF0IjoxNjk5OTk5OTk5LCJleHAiOjE2OTk5OTk5OTl9.sample_signature";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private AuthenticationService authenticationService;

  private void mockSuccessfulAuthentication(String username, String password) {
    AuthenticationResult authResult = new AuthenticationResult(1L, "ROLE_USER");
    CreateTokenPairResult tokenResult =
        new CreateTokenPairResult(SAMPLE_ACCESS_TOKEN, SAMPLE_REFRESH_TOKEN);

    given(authenticationService.getUserByCredentialInfo(any(CredentialCommand.class)))
        .willReturn(authResult);
    given(authenticationService.createAuthenticationToken(1L)).willReturn(tokenResult);
    willDoNothing().given(authenticationService).saveRefreshToken(any(SaveRefreshCommand.class));
  }

  private void mockFailedAuthentication(String code, String message) {
    willThrow(new AuthenticationException(code, message))
        .given(authenticationService)
        .getUserByCredentialInfo(any(CredentialCommand.class));
  }

  @Test
  @DisplayName("로그인 성공 - 유효한 자격 증명")
  void should_ReturnAccessToken_When_ValidCredentialsProvided() throws Exception {
    // given
    LoginRequest request = new LoginRequest(VALID_USERNAME, VALID_PASSWORD);
    mockSuccessfulAuthentication(VALID_USERNAME, VALID_PASSWORD);

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(SAMPLE_ACCESS_TOKEN))
        .andExpect(jsonPath("$.refreshToken").value(SAMPLE_REFRESH_TOKEN))
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.refreshToken").isNotEmpty());
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 사용자명")
  void should_ReturnUnauthorized_When_UsernameIsInvalid() throws Exception {
    // given
    LoginRequest request = new LoginRequest(INVALID_USERNAME, VALID_PASSWORD);
    mockFailedAuthentication("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다.");

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNSUCCESSFUL_AUTHENTICATION"))
        .andExpect(jsonPath("$.error.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 비밀번호")
  void should_ReturnUnauthorized_When_PasswordIsInvalid() throws Exception {
    // given
    LoginRequest request = new LoginRequest(VALID_USERNAME, INVALID_PASSWORD);
    mockFailedAuthentication("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다.");

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNSUCCESSFUL_AUTHENTICATION"));
  }

  @Test
  @DisplayName("로그인 실패 - 사용자명이 null (JSON 파싱은 성공하지만 validation 실패)")
  void should_ReturnUnauthorized_When_UsernameIsNull() throws Exception {
    // given
    LoginRequest request = new LoginRequest(null, VALID_PASSWORD);
    mockFailedAuthentication("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다.");

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNSUCCESSFUL_AUTHENTICATION"));
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호가 null")
  void should_ReturnUnauthorized_When_PasswordIsNull() throws Exception {
    // given
    LoginRequest request = new LoginRequest(VALID_USERNAME, null);
    mockFailedAuthentication("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다.");

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNSUCCESSFUL_AUTHENTICATION"));
  }

  @Test
  @DisplayName("로그인 실패 - 사용자명이 빈 문자열")
  void should_ReturnUnauthorized_When_UsernameIsBlank() throws Exception {
    // given
    LoginRequest request = new LoginRequest("", VALID_PASSWORD);
    mockFailedAuthentication("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다.");

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNSUCCESSFUL_AUTHENTICATION"));
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호가 빈 문자열")
  void should_ReturnUnauthorized_When_PasswordIsBlank() throws Exception {
    // given
    LoginRequest request = new LoginRequest(VALID_USERNAME, "");
    mockFailedAuthentication("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다.");

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNSUCCESSFUL_AUTHENTICATION"));
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 JSON 형식")
  void should_ReturnUnauthorized_When_JsonFormatIsInvalid() throws Exception {
    // given
    String invalidJson = "{ invalid json }";

    // when & then
    mockMvc
        .perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNSUCCESSFUL_AUTHENTICATION"));
  }

  @Test
  @DisplayName("로그인 실패 - 존재하지 않는 사용자")
  void should_ReturnUnauthorized_When_UserDoesNotExist() throws Exception {
    // given
    LoginRequest request = new LoginRequest("nonexistentuser", VALID_PASSWORD);
    mockFailedAuthentication("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다.");

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNSUCCESSFUL_AUTHENTICATION"));
  }

  @Test
  @DisplayName("JWT 토큰 형식 검증 - 발급된 토큰이 JWT 형식인지 확인")
  void should_ReturnJwtFormattedTokens_When_LoginSuccessful() throws Exception {
    // given
    LoginRequest request = new LoginRequest(VALID_USERNAME, VALID_PASSWORD);
    mockSuccessfulAuthentication(VALID_USERNAME, VALID_PASSWORD);

    // when & then
    mockMvc
        .perform(
            post(LOGIN_URL)
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
