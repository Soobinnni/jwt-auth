package com.auth.jwt.user.domain.vo;

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
      throw new IllegalArgumentException("사용자 식별자가 비어있습니다. 사용자 식별자는 필수입니다.");
    }
    if (value <= 0) {
      throw new IllegalArgumentException("사용자 식별자는 양수여야 합니다.");
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
