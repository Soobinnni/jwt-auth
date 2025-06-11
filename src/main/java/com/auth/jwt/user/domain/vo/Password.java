package com.auth.jwt.user.domain.vo;

import com.auth.jwt.user.domain.PasswordEncryptionProvider;

public class Password {
  private static final int MIN_LENGTH = 8;
  private static final int MAX_LENGTH = 20;
  private static final String PASSWORD_PATTERN =
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?])(.{"
          + MIN_LENGTH
          + ","
          + MAX_LENGTH
          + "})$";
  private static final String INVALID_LENGTH_MESSAGE =
      "비밀번호는 최소 " + MIN_LENGTH + " 길이와, 최대 " + MAX_LENGTH + " 길이여야 합니다.";

  private final String value;

  private Password(String value) {
    this.value = value;
  }

  public static Password of(
      String plainPassword, PasswordEncryptionProvider passwordEncryptionProvider) {
    validatePassword(plainPassword);
    return new Password(passwordEncryptionProvider.encode(plainPassword));
  }

  private static void validatePassword(String plainPassword) {
    if (plainPassword == null || plainPassword.isBlank()) {
      throw new IllegalArgumentException("비밀번호가 비어있습니다. 비밀번호는 필수입니다.");
    }

    if (!plainPassword.matches(PASSWORD_PATTERN)) {
      throw new IllegalArgumentException(INVALID_LENGTH_MESSAGE);
    }
  }

  public boolean matches(
      String plainPassword, PasswordEncryptionProvider passwordEncryptionService) {
    return passwordEncryptionService.matches(plainPassword, this.value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Password password = (Password) obj;
    return value.equals(password.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "...";
  }
}
