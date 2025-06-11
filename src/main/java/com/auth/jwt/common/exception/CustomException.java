package com.auth.jwt.common.exception;

public class CustomException extends RuntimeException {
  private final ExceptionDetail exceptionDetail;

  public CustomException(ExceptionDetail exceptionDetail) {
    super(exceptionDetail.getMessage());
    this.exceptionDetail = exceptionDetail;
  }

  public ExceptionDetail getErrorDetail() {
    return exceptionDetail;
  }
}
