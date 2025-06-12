package com.auth.jwt.user.domain;

import static org.assertj.core.api.Assertions.*;

import com.auth.jwt.user.domain.exception.UserEmptyException;
import com.auth.jwt.user.domain.exception.UserInvalidLengthException;
import com.auth.jwt.user.domain.vo.Username;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[UsernameTest] Username VO 검증 테스트")
class UsernameTest {

  @Test
  @DisplayName("Username 생성 성공 - 유효한 길이")
  void createUsernameSuccess() {
    // given
    String validUsername = "ab"; // 최소 길이 2
    String validLongUsername = "a".repeat(50); // 최대 길이 50

    // when & then
    assertThatCode(() -> Username.of(validUsername)).doesNotThrowAnyException();
    assertThatCode(() -> Username.of(validLongUsername)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Username 생성 실패 - 길이 부족")
  void createUsernameFailTooShort() {
    // given
    String shortUsername = "a"; // 1글자

    // when & then
    assertThatThrownBy(() -> Username.of(shortUsername))
        .isInstanceOf(UserInvalidLengthException.class)
        .hasMessageContaining("아이디가 최소 2 길이와, 최대 50 길이여야 합니다.");
  }

  @Test
  @DisplayName("Username 생성 실패 - 길이 초과")
  void createUsernameFailTooLong() {
    // given
    String longUsername = "a".repeat(51); // 51글자

    // when & then
    assertThatThrownBy(() -> Username.of(longUsername))
        .isInstanceOf(UserInvalidLengthException.class)
        .hasMessageContaining("아이디가 최소 2 길이와, 최대 50 길이여야 합니다.");
  }

  @Test
  @DisplayName("Username 생성 실패 - null 입력")
  void createUsernameFailNull() {
    // when & then
    assertThatThrownBy(() -> Username.of(null))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("아이디가 비어있습니다. 아이디는 필수입니다.");
  }

  @Test
  @DisplayName("Username 생성 실패 - 빈 문자열")
  void createUsernameFailBlank() {
    // when & then
    assertThatThrownBy(() -> Username.of(""))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("아이디가 비어있습니다. 아이디는 필수입니다.");

    assertThatThrownBy(() -> Username.of("   "))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("아이디가 비어있습니다. 아이디는 필수입니다.");
  }
}
