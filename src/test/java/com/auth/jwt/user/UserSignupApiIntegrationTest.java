package com.auth.jwt.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth.jwt.user.application.UserCommandService;
import com.auth.jwt.user.application.dto.command.SignupCommand;
import com.auth.jwt.user.application.exception.UserAlreadyExistsException;
import com.auth.jwt.user.domain.entity.Role;
import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.vo.Nickname;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import com.auth.jwt.user.presentation.dto.request.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
@DisplayName("[UserSignupApiIntegrationTest] 회원가입 API 통합 테스트")
class UserSignupApiIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private UserCommandService userCommandService;

  private User createMockUser(Long id, String username, String nickname) {
    return new User(UserId.of(id), Username.of(username), null, Nickname.of(nickname), Role.USER);
  }

  @Test
  @DisplayName("회원가입 성공 - 유효한 입력 데이터")
  void should_CreateUser_When_ValidInputProvided() throws Exception {
    // given
    User mockUser = createMockUser(1L, "testuser", "TestNick");
    SignupRequest request = new SignupRequest("testuser", "password123", "TestNick");
    given(userCommandService.signup(ArgumentMatchers.any(SignupCommand.class)))
        .willReturn(mockUser);

    // when & then
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.nickname").value("TestNick"))
        .andExpect(jsonPath("$.role").value("일반 사용자"))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(header().string("Location", "/users/1"));
  }

  @Test
  @DisplayName("회원가입 실패 - 이미 존재하는 사용자명")
  void should_RejectSignup_When_UsernameAlreadyExists() throws Exception {
    // given
    SignupRequest request = new SignupRequest("duplicateuser", "password123", "TestNick");
    willThrow(new UserAlreadyExistsException()).given(userCommandService).signup(any());

    // when & then
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"))
        .andExpect(jsonPath("$.error.message").value("이미 가입된 사용자입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 사용자명이 null")
  void should_RejectSignup_When_UsernameIsNull() throws Exception {
    // given
    SignupRequest request = new SignupRequest(null, "password123", "TestNick");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.username").value("아이디는 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 사용자명이 빈 문자열")
  void should_RejectSignup_When_UsernameIsBlank() throws Exception {
    // given
    SignupRequest request = new SignupRequest("", "password123", "TestNick");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.username").value("아이디는 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 비밀번호가 null")
  void should_RejectSignup_When_PasswordIsNull() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", null, "TestNick");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.password").value("비밀번호는 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 비밀번호가 빈 문자열")
  void should_RejectSignup_When_PasswordIsBlank() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", "", "TestNick");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.password").value("비밀번호는 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 닉네임이 null")
  void should_RejectSignup_When_NicknameIsNull() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", "password123", null);

    // when & then
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.nickname").value("닉네임은 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 닉네임이 빈 문자열")
  void should_RejectSignup_When_NicknameIsBlank() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", "password123", "");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.nickname").value("닉네임은 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 잘못된 JSON 형식")
  void should_RejectSignup_When_JsonFormatIsInvalid() throws Exception {
    // given
    String invalidJson = "{ invalid json }";

    // when & then
    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("회원가입 실패 - Content-Type 헤더 누락")
  void should_RejectSignup_When_ContentTypeHeaderMissing() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", "password123", "TestNick");

    // when & then
    mockMvc
        .perform(post("/users").content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnsupportedMediaType());
  }
}
