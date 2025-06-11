package com.auth.jwt.user.domain.vo;

public class Username {
  private static final int MIN_LENGTH = 2;
  private static final int MAX_LENGTH = 50;
  private static final String INVALID_LENGTH_MESSAGE =
      "로그인 아이디는 최소 " + MIN_LENGTH + " 길이와, 최대 " + MAX_LENGTH + " 길이여야 합니다.";

  private final String value;

  private Username(String value) {
    this.value = value;
  }

  public static Username of(String value) {
    validateUsername(value);
    return new Username(value);
  }

  private static void validateUsername(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("로그인 아이디가 비어있습니다. 로그인 아이디는 필수입니다.");
    }

    if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(INVALID_LENGTH_MESSAGE);
    }
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Username username = (Username) obj;
    return value.equals(username.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }
}
