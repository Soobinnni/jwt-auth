package com.auth.jwt.auth.application.model;

import java.time.LocalDateTime;

public record RefreshToken(String tokenValue, Long userId, LocalDateTime expiry) {
  public static RefreshToken create(String tokenValue, Long userId, LocalDateTime expiry) {
    if (tokenValue == null || tokenValue.trim().isEmpty()) {
      throw new IllegalArgumentException("토큰 값은 비어있을 수 없습니다.");
    }
    if (userId == null) {
      throw new IllegalArgumentException("사용자 ID는 필수입니다.");
    }
    if (expiry == null) {
      throw new IllegalArgumentException("만료 시간은 필수입니다.");
    }
    return new RefreshToken(tokenValue, userId, expiry);
  }

  public boolean isExpired(LocalDateTime currentTime) {
    return currentTime.isAfter(expiry);
  }
}
