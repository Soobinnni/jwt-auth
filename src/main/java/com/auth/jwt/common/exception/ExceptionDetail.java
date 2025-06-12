package com.auth.jwt.common.exception;

public interface ExceptionDetail {
  static ExceptionDetail of(String code, String message) {
    return new ExceptionDetail() {
      @Override
      public String getCode() {
        return code;
      }

      @Override
      public String getMessage() {
        return message;
      }
    };
  }

  String getCode();

  String getMessage();
}
