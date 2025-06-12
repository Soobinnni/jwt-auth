package com.auth.jwt.auth.application.exception;

import com.auth.jwt.common.exception.BusinessException;
import com.auth.jwt.common.exception.ExceptionDetail;

public class AuthorizationException extends BusinessException {
  public AuthorizationException(String code, String message) {
    super(ExceptionDetail.of(code, message));
  }
}
