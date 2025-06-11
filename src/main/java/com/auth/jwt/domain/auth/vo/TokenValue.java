package com.auth.jwt.domain.auth.vo;

public class TokenValue {
  private final String value;

  private TokenValue(String value) {
    this.value = value;
  }

  public static TokenValue of(String value) {
    validateToken(value);
    return new TokenValue(value);
  }

  private static void validateToken(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("토큰 값이 비어있습니다. 토큰 값은 필수입니다.");
    }
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    TokenValue that = (TokenValue) obj;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "";
  }
}
