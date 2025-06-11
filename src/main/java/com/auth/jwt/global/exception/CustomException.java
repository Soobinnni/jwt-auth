package com.auth.jwt.global.exception;

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
