package com.auth.jwt.auth.application.exception;

import com.auth.jwt.common.exception.BusinessException;
import com.auth.jwt.common.exception.ExceptionDetail;

public class TokenExpiredException extends BusinessException {
  public TokenExpiredException() {
    super(ExceptionDetail.of("TOKEN_EXPIRED", "토큰이 만료되었습니다."));
  }
}
