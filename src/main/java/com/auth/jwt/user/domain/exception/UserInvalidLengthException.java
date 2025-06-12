package com.auth.jwt.user.domain.exception;

public class UserInvalidLengthException extends UserInvalidValueException {
  public UserInvalidLengthException(UserDomainField type, int minLength, int maxLength) {
    super(
        type,
        type.getKoreanName()
            + type.getKoreanSubjectMarker()
            + " 최소 "
            + minLength
            + " 길이와, 최대 "
            + maxLength
            + " 길이여야 합니다.");
  }

  public UserInvalidLengthException(UserDomainField type, String customMessage) {
    super(type, customMessage);
  }

  @Override
  public String getCode() {
    return "INVALID_LENGTH_" + type.name();
  }
}
