package com.auth.jwt.user.domain.vo;

import com.auth.jwt.user.domain.exception.UserDomainField;
import com.auth.jwt.user.domain.exception.UserEmptyException;
import com.auth.jwt.user.domain.exception.UserInvalidValueException;

public class UserId {
  private final Long value;

  private UserId(Long value) {
    this.value = value;
  }

  public static UserId of(Long value) {
    validateUserId(value);
    return new UserId(value);
  }

  private static void validateUserId(Long value) {
    if (value == null) {
      throw new UserEmptyException(UserDomainField.ID);
    }
    if (value <= 0) {
      throw new UserInvalidValueException(UserDomainField.ID, "사용자 식별자는 양수여야 합니다.");
    }
  }

  public Long getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    UserId userId = (UserId) obj;
    return value.equals(userId.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
