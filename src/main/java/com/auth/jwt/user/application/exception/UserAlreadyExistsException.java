package com.auth.jwt.user.application.exception;

import com.auth.jwt.common.exception.DuplicateResourceException;
import com.auth.jwt.common.exception.ExceptionDetail;

public class UserAlreadyExistsException extends DuplicateResourceException {
  public UserAlreadyExistsException() {
    super(ExceptionDetail.of("USER_ALREADY_EXISTS", "이미 가입된 사용자입니다."));
  }
}
