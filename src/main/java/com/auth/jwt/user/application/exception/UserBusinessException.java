package com.auth.jwt.user.application.exception;

import com.auth.jwt.common.exception.ExceptionDetail;
import com.auth.jwt.common.exception.InvalidException;

public class UserBusinessException extends InvalidException {
  public UserBusinessException(String code, String message) {
    super(ExceptionDetail.of(code, message));
  }
}
