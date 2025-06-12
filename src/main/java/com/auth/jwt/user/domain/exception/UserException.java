package com.auth.jwt.user.domain.exception;

public class UserException extends RuntimeException {
  public UserException(String message) {
    super(message);
  }

  public UserException(String message, Throwable cause) {
    super(message, cause);
  }

  public String getCode() {
    return "USER_DOMAIN_EXCEPTION";
  }
}
