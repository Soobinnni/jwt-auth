package com.auth.jwt.user.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.auth.jwt.user.domain.exception.UserEmptyException;
import com.auth.jwt.user.domain.exception.UserInvalidLengthException;
import com.auth.jwt.user.domain.exception.UserInvalidValueException;
import com.auth.jwt.user.domain.service.PasswordEncryptionProvider;
import com.auth.jwt.user.domain.vo.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("[PasswordTest] Password VO 검증 테스트")
class PasswordTest {

  private final PasswordEncryptionProvider encryptionProvider =
      Mockito.mock(PasswordEncryptionProvider.class);

  @BeforeEach
  void setUp() {
    given(encryptionProvider.encode(any())).willReturn("encrypted-password");
  }

  @Test
  @DisplayName("Password 생성 성공 - 유효한 패스워드")
  void createPasswordSuccess() {
    // given
    String validPassword = "password123"; // 영문자 + 숫자, 8-20자

    // when & then
    assertThatCode(() -> Password.of(validPassword, encryptionProvider)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Password 생성 실패 - 길이 부족")
  void createPasswordFailTooShort() {
    // given
    String shortPassword = "pass12"; // 6글자

    // when & then
    assertThatThrownBy(() -> Password.of(shortPassword, encryptionProvider))
        .isInstanceOf(UserInvalidLengthException.class)
        .hasMessageContaining("최소 8 길이와, 최대 20 길이여야 합니다");
  }

  @Test
  @DisplayName("Password 생성 실패 - 길이 초과")
  void createPasswordFailTooLong() {
    // given
    String longPassword = "a".repeat(21);

    // when & then
    assertThatThrownBy(() -> Password.of(longPassword, encryptionProvider))
        .isInstanceOf(UserInvalidLengthException.class)
        .hasMessageContaining("최소 8 길이와, 최대 20 길이여야 합니다");
  }

  @Test
  @DisplayName("Password 생성 실패 - 숫자 없음")
  void createPasswordFailNoNumber() {
    // given
    String passwordWithoutNumber = "password"; // 숫자 없음

    // when & then
    assertThatThrownBy(() -> Password.of(passwordWithoutNumber, encryptionProvider))
        .isInstanceOf(UserInvalidValueException.class)
        .hasMessageContaining("대문자 또는 소문자와 숫자 조합으로 이루어져야 합니다");
  }

  @Test
  @DisplayName("Password 생성 실패 - 영문자 없음")
  void createPasswordFailNoLetter() {
    // given
    String passwordWithoutLetter = "12345678"; // 영문자 없음

    // when & then
    assertThatThrownBy(() -> Password.of(passwordWithoutLetter, encryptionProvider))
        .isInstanceOf(UserInvalidValueException.class)
        .hasMessageContaining("대문자 또는 소문자와 숫자 조합으로 이루어져야 합니다");
  }

  @Test
  @DisplayName("Password 생성 실패 - null 입력")
  void createPasswordFailNull() {
    // when & then
    assertThatThrownBy(() -> Password.of(null, encryptionProvider))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("비밀번호가 비어있습니다");
  }
}
