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
  @DisplayName("사용자명 생성 성공 - 유효한 길이 범위")
  void should_CreateUsername_When_ValidLengthProvided() {
    // given
    String minLengthUsername = "ab"; // 최소 길이 2
    String maxLengthUsername = "a".repeat(50); // 최대 길이 50

    // when & then
    assertThatCode(() -> Username.of(minLengthUsername)).doesNotThrowAnyException();
    assertThatCode(() -> Username.of(maxLengthUsername)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("사용자명 생성 실패 - 최소 길이 미달")
  void should_ThrowException_When_UsernameTooShort() {
    // given
    String shortUsername = "a"; // 1글자

    // when & then
    assertThatThrownBy(() -> Username.of(shortUsername))
        .isInstanceOf(UserInvalidLengthException.class)
        .hasMessageContaining("아이디가 최소 2 길이와, 최대 50 길이여야 합니다.");
  }

  @Test
  @DisplayName("사용자명 생성 실패 - 최대 길이 초과")
  void should_ThrowException_When_UsernameTooLong() {
    // given
    String longUsername = "a".repeat(51); // 51글자

    // when & then
    assertThatThrownBy(() -> Username.of(longUsername))
        .isInstanceOf(UserInvalidLengthException.class)
        .hasMessageContaining("아이디가 최소 2 길이와, 최대 50 길이여야 합니다.");
  }

  @Test
  @DisplayName("사용자명 생성 실패 - null 값 입력")
  void should_ThrowException_When_UsernameIsNull() {
    // when & then
    assertThatThrownBy(() -> Username.of(null))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("아이디가 비어있습니다. 아이디는 필수입니다.");
  }

  @Test
  @DisplayName("사용자명 생성 실패 - 빈 문자열 또는 공백")
  void should_ThrowException_When_UsernameIsBlankOrWhitespace() {
    // when & then
    assertThatThrownBy(() -> Username.of(""))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("아이디가 비어있습니다. 아이디는 필수입니다.");

    assertThatThrownBy(() -> Username.of("   "))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("아이디가 비어있습니다. 아이디는 필수입니다.");
  }
}
