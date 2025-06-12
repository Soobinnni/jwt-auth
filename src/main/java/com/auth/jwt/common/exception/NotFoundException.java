package com.auth.jwt.common.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException implements CustomException {
  private final ExceptionDetail exceptionDetail;

  public NotFoundException(ExceptionDetail exceptionDetail) {
    super(exceptionDetail.getMessage());
    this.exceptionDetail = exceptionDetail;
  }
}
