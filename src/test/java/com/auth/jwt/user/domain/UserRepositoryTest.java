package com.auth.jwt.user.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.repository.UserRepository;
import com.auth.jwt.user.domain.service.IdGenerator;
import com.auth.jwt.user.domain.service.PasswordEncryptionProvider;
import com.auth.jwt.user.domain.vo.Username;
import com.auth.jwt.user.infrastructure.persistence.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("[UserRepositoryTest] 사용자 저장소 테스트")
class UserRepositoryTest {

  private final UserRepository userRepository = new InMemoryUserRepository();
  private final PasswordEncryptionProvider encryptionProvider =
      Mockito.mock(PasswordEncryptionProvider.class);
  private final IdGenerator idGenerator = () -> 1L;

  @BeforeEach
  void setUp() {
    given(encryptionProvider.encode(any())).willReturn("encrypted-password");
  }

  @Test
  @DisplayName("사용자 저장 및 존재 여부 확인 성공")
  void should_SaveAndConfirmExistence_When_UserIsStored() {
    // given
    User user = User.create(idGenerator, "testuser", "password123", encryptionProvider, "TestNick");

    // when
    User savedUser = userRepository.save(user);
    boolean exists = userRepository.existsByUsername(Username.of("testuser"));

    // then
    assertThat(savedUser).isEqualTo(user);
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("사용자 존재 여부 확인 실패 - 존재하지 않는 사용자명")
  void should_ReturnFalse_When_UsernameDoesNotExist() {
    // when
    boolean exists = userRepository.existsByUsername(Username.of("nonexistent"));

    // then
    assertThat(exists).isFalse();
  }
}
