package com.auth.jwt.domain.auth;

import com.auth.jwt.domain.auth.vo.TokenExpiry;
import com.auth.jwt.domain.auth.vo.TokenValue;
import com.auth.jwt.domain.user.vo.UserId;
import lombok.Getter;

@Getter
public class RefreshToken {
  private final UserId userId;
  private final TokenValue tokenValue;
  private final TokenExpiry expiry;

  private RefreshToken(TokenValue tokenValue, UserId userId, TokenExpiry expiry) {
    this.tokenValue = tokenValue;
    this.userId = userId;
    this.expiry = expiry;
  }

  public static RefreshToken create(TokenValue tokenValue, UserId userId, TokenExpiry expiry) {
    return new RefreshToken(tokenValue, userId, expiry);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    RefreshToken that = (RefreshToken) obj;
    return tokenValue.equals(that.tokenValue);
  }

  @Override
  public int hashCode() {
    return tokenValue.hashCode();
  }
}
