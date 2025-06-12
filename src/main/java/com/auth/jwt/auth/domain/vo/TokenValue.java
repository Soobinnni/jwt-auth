package com.auth.jwt.auth.domain.vo;

public class TokenValue {
  private final String value;

  private TokenValue(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("토큰 값은 비어있을 수 없습니다.");
    }
    this.value = value;
  }

  public static TokenValue of(String value) {
    return new TokenValue(value);
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
