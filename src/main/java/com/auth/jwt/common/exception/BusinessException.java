package com.auth.jwt.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException implements CustomException {
  private final ExceptionDetail exceptionDetail;

  public BusinessException(ExceptionDetail exceptionDetail) {
    super(exceptionDetail.getMessage());
    this.exceptionDetail = exceptionDetail;
  }
}
