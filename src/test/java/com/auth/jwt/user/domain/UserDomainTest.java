package com.auth.jwt.user.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.auth.jwt.user.domain.entity.Role;
import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.service.IdGenerator;
import com.auth.jwt.user.domain.service.PasswordEncryptionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("[UserDomainTest] User 도메인 엔티티 검증 테스트")
class UserDomainTest {

  private final IdGenerator idGenerator = () -> 1L;
  private final PasswordEncryptionProvider encryptionProvider =
      Mockito.mock(PasswordEncryptionProvider.class);

  @BeforeEach
  void setUp() {
    given(encryptionProvider.encode(any())).willReturn("encrypted-password");
  }

  @Test
  @DisplayName("사용자 생성 성공 - 유효한 입력값")
  void should_CreateUser_When_ValidInputProvided() {
    // when
    User user =
        User.create(idGenerator, "validuser", "password123", encryptionProvider, "ValidNick");

    // then
    assertThat(user.getUsername().getValue()).isEqualTo("validuser");
    assertThat(user.getNickname().getValue()).isEqualTo("ValidNick");
    assertThat(user.getRole()).isEqualTo(Role.USER);
  }
}
