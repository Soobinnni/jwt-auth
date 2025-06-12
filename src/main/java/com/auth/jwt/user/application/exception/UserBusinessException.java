package com.auth.jwt.user.application.exception;

import com.auth.jwt.common.exception.BusinessException;
import com.auth.jwt.common.exception.ExceptionDetail;

public class UserBusinessException extends BusinessException {
  public UserBusinessException(String code, String message) {
    super(ExceptionDetail.of(code, message));
  }
}
