package com.auth.jwt.user.application;

import static org.assertj.core.api.Assertions.*;

import com.auth.jwt.user.application.dto.command.SignupCommand;
import com.auth.jwt.user.application.exception.UserAlreadyExistsException;
import com.auth.jwt.user.domain.entity.Role;
import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.repository.UserRepository;
import com.auth.jwt.user.domain.vo.Username;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("[UserSignupServiceTest] 회원가입 서비스 테스트")
class UserSignupServiceTest {

  @Autowired private UserCommandService userCommandService;
  @Autowired private UserRepository userRepository;

  @Test
  @DisplayName("회원가입 성공 - 유효한 사용자 정보")
  void should_CreateUser_When_ValidUserInfoProvided() {
    // given
    SignupCommand command = new SignupCommand("validuser", "password123", "ValidNick");

    // when
    User result = userCommandService.signup(command);

    // then
    assertThat(result.getUsername().getValue()).isEqualTo("validuser");
    assertThat(result.getNickname().getValue()).isEqualTo("ValidNick");
    assertThat(result.getRole()).isEqualTo(Role.USER);
    assertThat(userRepository.existsByUsername(Username.of("validuser"))).isTrue();
  }

  @Test
  @DisplayName("회원가입 실패 - 중복된 사용자명")
  void should_ThrowException_When_UsernameAlreadyExists() {
    // given
    SignupCommand firstCommand = new SignupCommand("duplicateuser", "password123", "FirstNick");
    SignupCommand secondCommand = new SignupCommand("duplicateuser", "password456", "SecondNick");

    userCommandService.signup(firstCommand);

    // when & then
    assertThatThrownBy(() -> userCommandService.signup(secondCommand))
        .isInstanceOf(UserAlreadyExistsException.class);
  }
}
