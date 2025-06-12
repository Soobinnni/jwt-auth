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
  @DisplayName("Nickname 생성 성공 - 유효한 길이")
  void createNicknameSuccess() {
    // given
    String validNickname = "N"; // 최소 길이 1
    String validLongNickname = "a".repeat(30); // 최대 길이 30

    // when & then
    assertThatCode(() -> Nickname.of(validNickname)).doesNotThrowAnyException();
    assertThatCode(() -> Nickname.of(validLongNickname)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Nickname 생성 실패 - 길이 초과")
  void createNicknameFailTooLong() {
    // given
    String longNickname = "a".repeat(31); // 31글자

    // when & then
    assertThatThrownBy(() -> Nickname.of(longNickname))
        .isInstanceOf(UserInvalidLengthException.class)
        .hasMessageContaining("최소 1 길이와, 최대 30 길이여야 합니다");
  }

  @Test
  @DisplayName("Nickname 생성 실패 - null 입력")
  void createNicknameFailNull() {
    // when & then
    assertThatThrownBy(() -> Nickname.of(null))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("닉네임이 비어있습니다");
  }

  @Test
  @DisplayName("Nickname 생성 실패 - 빈 문자열")
  void createNicknameFailBlank() {
    // when & then
    assertThatThrownBy(() -> Nickname.of(""))
        .isInstanceOf(UserEmptyException.class)
        .hasMessageContaining("닉네임이 비어있습니다");
  }
}
