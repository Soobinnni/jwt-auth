package com.auth.jwt.user.domain.vo;

import com.auth.jwt.user.domain.exception.UserDomainField;
import com.auth.jwt.user.domain.exception.UserEmptyException;
import com.auth.jwt.user.domain.exception.UserInvalidLengthException;
import com.auth.jwt.user.domain.exception.UserInvalidValueException;
import com.auth.jwt.user.domain.service.PasswordEncryptionProvider;

public class Password {
  private static final int MIN_LENGTH = 8;
  private static final int MAX_LENGTH = 20;
  private static final String PASSWORD_PATTERN =
      "^(?=.*[0-9])(?=.*[a-zA-Z]).{" + MIN_LENGTH + "," + MAX_LENGTH + "}$";

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
      throw new UserEmptyException(UserDomainField.PASSWORD);
    }

    if (plainPassword.length() < MIN_LENGTH || plainPassword.length() > MAX_LENGTH) {
      throw new UserInvalidLengthException(UserDomainField.PASSWORD, MIN_LENGTH, MAX_LENGTH);
    }

    if (!plainPassword.matches(PASSWORD_PATTERN)) {
      throw new UserInvalidValueException(
          UserDomainField.PASSWORD, "비밀번호는 대문자 또는 소문자와 숫자 조합으로 이루어져야 합니다.");
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
