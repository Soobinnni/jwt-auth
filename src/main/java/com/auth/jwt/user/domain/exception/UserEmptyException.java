package com.auth.jwt.user.domain.exception;

public class UserEmptyException extends UserInvalidValueException {
  public UserEmptyException(UserDomainField type) {
    super(
        type,
        type.getKoreanName()
            + type.getKoreanSubjectMarker()
            + " 비어있습니다. "
            + type.getKoreanName()
            + type.getKoreanTopicMarker()
            + " 필수입니다.");
  }

  public UserEmptyException(UserDomainField type, String customMessage) {
    super(type, customMessage);
  }

  @Override
  public String getCode() {
    return "EMPTY_" + type.name();
  }
}
