package com.auth.jwt.user.domain.vo;

public class Nickname {
  private static final int MIN_LENGTH = 1;
  private static final int MAX_LENGTH = 30;
  private static final String INVALID_LENGTH_MESSAGE =
      "닉네임은 최소 " + MIN_LENGTH + " 길이와, 최대 " + MAX_LENGTH + " 길이여야 합니다.";

  private final String value;

  private Nickname(String value) {
    this.value = value;
  }

  public static Nickname of(String value) {
    validateNickname(value);
    return new Nickname(value);
  }

  private static void validateNickname(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("닉네임이 비어있습니다. 닉네임은 필수입니다.");
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
    Nickname nickname = (Nickname) obj;
    return value.equals(nickname.value);
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
