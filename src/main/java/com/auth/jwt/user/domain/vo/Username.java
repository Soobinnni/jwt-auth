package com.auth.jwt.user.domain.vo;

import com.auth.jwt.user.domain.exception.UserDomainField;
import com.auth.jwt.user.domain.exception.UserEmptyException;
import com.auth.jwt.user.domain.exception.UserInvalidLengthException;

public class Username {
  private static final int MIN_LENGTH = 2;
  private static final int MAX_LENGTH = 50;

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
      throw new UserEmptyException(UserDomainField.USERNAME);
    }

    if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
      throw new UserInvalidLengthException(UserDomainField.USERNAME, MIN_LENGTH, MAX_LENGTH);
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
