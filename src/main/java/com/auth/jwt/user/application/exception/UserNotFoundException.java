package com.auth.jwt.user.application.exception;

import com.auth.jwt.common.exception.ExceptionDetail;
import com.auth.jwt.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
  public UserNotFoundException(String code, String message) {
    super(ExceptionDetail.of(code, message));
  }

  public UserNotFoundException() {
    super(ExceptionDetail.of("NOT_FOUND_USER", "사용자를 찾을 수 없습니다."));
  }
}
