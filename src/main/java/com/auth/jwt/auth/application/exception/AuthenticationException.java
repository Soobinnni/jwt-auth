package com.auth.jwt.auth.application.exception;

import com.auth.jwt.common.exception.BusinessException;
import com.auth.jwt.common.exception.ExceptionDetail;

public class AuthenticationException extends BusinessException {
  public AuthenticationException(String code, String message) {
    super(ExceptionDetail.of(code, message));
  }
}
