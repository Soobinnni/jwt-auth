package com.auth.jwt.auth;

import static org.assertj.core.api.Assertions.*;

import com.auth.jwt.auth.application.exception.TokenExpiredException;
import com.auth.jwt.auth.infrastructure.config.JwtProperties;
import com.auth.jwt.auth.infrastructure.jwt.JwtTokenIssuerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
    properties = {"jwt.accessTokenExpiration=1000", "jwt.refreshTokenExpiration=1000"})
@DisplayName("[JwtExpirationIntegrationTest] JWT 토큰 만료 테스트")
class JwtExpirationIntegrationTest {

  @Autowired private JwtTokenIssuerPort jwtTokenIssuerPort;
  @Autowired private JwtProperties jwtProperties;

  @Test
  @DisplayName("액세스 토큰 만료 테스트 - 1초 만료 시간 설정")
  void should_ThrowException_When_AccessTokenExpires() throws InterruptedException {
    // given
    Long userId = 1L;
    String username = "testuser";
    String authority = "ROLE_USER";

    String accessToken = jwtTokenIssuerPort.generateAccessToken(userId, username, authority);

    Thread.sleep(1500);

    // when & then
    assertThatThrownBy(() -> jwtTokenIssuerPort.validateToken(accessToken))
        .isInstanceOf(TokenExpiredException.class)
        .hasMessageContaining("토큰이 만료되었습니다");
  }

  @Test
  @DisplayName("리프레시 토큰 만료 테스트")
  void should_ThrowException_When_RefreshTokenExpires() throws InterruptedException {
    // given
    Long userId = 1L;
    String username = "testuser";

    String refreshToken = jwtTokenIssuerPort.generateRefreshToken(userId, username);

    Thread.sleep(1500);

    // when & then
    assertThatThrownBy(() -> jwtTokenIssuerPort.validateRefreshToken(refreshToken))
        .isInstanceOf(TokenExpiredException.class);
  }

  @Test
  @DisplayName("토큰 만료 전에는 정상 검증")
  void should_ValidateSuccessfully_When_TokenNotExpired() {
    // given
    Long userId = 1L;
    String username = "testuser";
    String authority = "ROLE_USER";

    String accessToken = jwtTokenIssuerPort.generateAccessToken(userId, username, authority);

    // when
    boolean isValid = jwtTokenIssuerPort.validateToken(accessToken);

    // then
    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("설정된 만료 시간 확인")
  void should_UseConfiguredExpirationTime() {
    // when & then
    assertThat(jwtProperties.getAccessTokenExpiration()).isEqualTo(1000L);
    assertThat(jwtProperties.getRefreshTokenExpiration()).isEqualTo(1000L);
  }
}
