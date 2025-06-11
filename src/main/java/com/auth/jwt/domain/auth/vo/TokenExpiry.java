package com.auth.jwt.domain.auth.vo;

import java.time.LocalDateTime;

public class TokenExpiry {
  private final LocalDateTime expiryTime;

  private TokenExpiry(LocalDateTime expiryTime) {
    this.expiryTime = expiryTime;
  }

  public static TokenExpiry of(LocalDateTime expiryTime) {
    validateExpiryTime(expiryTime);
    return new TokenExpiry(expiryTime);
  }

  private static void validateExpiryTime(LocalDateTime expiryTime) {
    if (expiryTime == null) {
      throw new IllegalArgumentException("만료 정보가 비어있습니다. 만료 정보는 필수입니다.");
    }
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiryTime);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    TokenExpiry that = (TokenExpiry) obj;
    return expiryTime.equals(that.expiryTime);
  }

  @Override
  public int hashCode() {
    return expiryTime.hashCode();
  }

  @Override
  public String toString() {
    return expiryTime.toString();
  }
}
