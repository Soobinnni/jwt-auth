package com.auth.jwt.common.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException implements CustomException {
  private final ExceptionDetail exceptionDetail;

  public DuplicateResourceException(ExceptionDetail exceptionDetail) {
    super(exceptionDetail.getMessage());
    this.exceptionDetail = exceptionDetail;
  }
}
