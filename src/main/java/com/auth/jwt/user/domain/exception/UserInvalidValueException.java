package com.auth.jwt.user.domain.exception;

public class UserInvalidValueException extends UserException {
  protected final UserDomainField type;

  public UserInvalidValueException(UserDomainField type, String message) {
    super(message);
    this.type = type;
  }

  public UserInvalidValueException(UserDomainField type, String message, Throwable cause) {
    super(message, cause);
    this.type = type;
  }

  @Override
  public String getCode() {
    return "INVALID_" + type.name() + "_VALUE";
  }
}
