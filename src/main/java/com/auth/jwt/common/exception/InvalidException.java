package com.auth.jwt.common.exception;

import lombok.Getter;

@Getter
public class InvalidException extends RuntimeException implements CustomException {
  private final ExceptionDetail exceptionDetail;

  public InvalidException(ExceptionDetail exceptionDetail) {
    super(exceptionDetail.getMessage());
    this.exceptionDetail = exceptionDetail;
  }
}
