package com.auth.jwt.user.domain;

import static org.assertj.core.api.Assertions.*;

import com.auth.jwt.user.domain.exception.UserEmptyException;
import com.auth.jwt.user.domain.exception.UserInvalidLengthException;
import com.auth.jwt.user.domain.vo.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[NicknameTest] Nickname VO 검증 테스트")
class NicknameTest {

  @Test
  @DisplayName("닉네임 생성 성공 - 유효한 길이 범위")
  void should_CreateNickname_When_ValidLengthProvided() {
    // given
    String minLengthNickname = "N"; // 최소 길이 1
    String maxLengthNickname = "a".repeat(30); // 최대 길이 30

    // when & then
    assertThatCode(() -> Nickname.of(minLengthNickname)).doesNotThrowAnyException();
    assertThatCode(() -> Nickname.of(maxLengthNickname)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("닉네임 생성 실패 - 최대 길이 초과")
  void should_ThrowException_When_NicknameExceedsMaxLength() {
    // given
    String tooLongNickname = "a".repeat(31); // 31글자

    // when & then
    assertThatThrownBy(() -> Nickname.of(tooLongNickname))
        .isInstanceOf(UserInvalidLengthException.class)
        .hasMessageContaining("최소 1 길이와, 최대 30 길이여야 합니다");
  }

  @Test
  @DisplayName("닉네임 생성 실패 - null 값 입력")
  void should_ThrowException_When_NicknameIsNull() {
    // when & then
    assertThatThrownBy(() -> Nickname.of(null))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("닉네임이 비어있습니다");
  }

  @Test
  @DisplayName("닉네임 생성 실패 - 빈 문자열 입력")
  void should_ThrowException_When_NicknameIsBlank() {
    // when & then
    assertThatThrownBy(() -> Nickname.of(""))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("닉네임이 비어있습니다");
  }
}
