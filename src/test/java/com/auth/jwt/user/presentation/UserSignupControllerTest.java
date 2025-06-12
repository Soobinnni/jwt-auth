package com.auth.jwt.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth.jwt.user.application.UserService;
import com.auth.jwt.user.application.dto.command.SignupCommand;
import com.auth.jwt.user.application.exception.UserAlreadyExistsException;
import com.auth.jwt.user.domain.entity.Role;
import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.vo.Nickname;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import com.auth.jwt.user.presentation.dto.request.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = UserController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(UserSignupControllerTest.TestConfig.class)
@DisplayName("[UserSignupControllerTest] 회원가입 테스트")
class UserSignupControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserService userService;

  @BeforeEach
  void setUp() {
    Mockito.reset(userService);
  }

  @Test
  @DisplayName("회원가입 성공 - 올바른 입력")
  void signupSuccess() throws Exception {
    // given
    User mockUser =
        new User(
            UserId.of(1L),
            Username.of("testuser"),
            null,
            Nickname.of("TestNick"),
            Set.of(Role.USER));
    SignupRequest request = new SignupRequest("testuser", "password123", "TestNick");
    given(userService.signup(ArgumentMatchers.any(SignupCommand.class))).willReturn(mockUser);

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.nickname").value("TestNick"))
        .andExpect(jsonPath("$.roles[0]").value("일반 사용자"))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(header().string("Location", "/users/1"));
  }

  @Test
  @DisplayName("회원가입 실패 - 사용자명이 null")
  void signupFailUsernameNull() throws Exception {
    // given
    SignupRequest request = new SignupRequest(null, "password123", "TestNick");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.username").value("아이디는 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 사용자명이 빈 문자열")
  void signupFailUsernameBlank() throws Exception {
    // given
    SignupRequest request = new SignupRequest("", "password123", "TestNick");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.username").value("아이디는 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 비밀번호가 null")
  void signupFailPasswordNull() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", null, "TestNick");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.password").value("비밀번호는 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 비밀번호가 빈 문자열")
  void signupFailPasswordBlank() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", "", "TestNick");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.password").value("비밀번호는 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 닉네임이 null")
  void signupFailNicknameNull() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", "password123", null);

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.nickname").value("닉네임은 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 닉네임이 빈 문자열")
  void signupFailNicknameBlank() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", "password123", "");

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.error.details.nickname").value("닉네임은 필수입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 이미 존재하는 사용자")
  void signupFailUserAlreadyExists() throws Exception {
    // given
    SignupRequest request = new SignupRequest("existinguser", "password123", "TestNick");
    willThrow(new UserAlreadyExistsException()).given(userService).signup(any());

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"))
        .andExpect(jsonPath("$.error.message").value("이미 가입된 사용자입니다."));
  }

  @Test
  @DisplayName("회원가입 실패 - 잘못된 JSON 형식")
  void signupFailInvalidJson() throws Exception {
    // given
    String invalidJson = "{ invalid json }";

    // when & then
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("회원가입 실패 - Content-Type이 없는 경우")
  void signupFailNoContentType() throws Exception {
    // given
    SignupRequest request = new SignupRequest("testuser", "password123", "TestNick");

    // when & then
    mockMvc
        .perform(post("/users").with(csrf()).content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnsupportedMediaType());
  }

  @TestConfiguration
  static class TestConfig {
    @Bean
    public UserService userService() {
      return Mockito.mock(UserService.class);
    }
  }
}
