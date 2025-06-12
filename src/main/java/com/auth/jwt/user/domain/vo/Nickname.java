package com.auth.jwt.user.domain.vo;

import com.auth.jwt.user.domain.exception.UserDomainField;
import com.auth.jwt.user.domain.exception.UserEmptyException;
import com.auth.jwt.user.domain.exception.UserInvalidLengthException;

public class Nickname {
  private static final int MIN_LENGTH = 1;
  private static final int MAX_LENGTH = 30;

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
      throw new UserEmptyException(UserDomainField.NICKNAME);
    }

    if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
      throw new UserInvalidLengthException(UserDomainField.NICKNAME, MIN_LENGTH, MAX_LENGTH);
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
