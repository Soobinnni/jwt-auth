package com.auth.jwt.common.dto;

import com.auth.jwt.common.exception.ExceptionDetail;

public record ErrorResponse(Error error) {
  public static ErrorResponse from(ExceptionDetail detail) {
    return new ErrorResponse(new Error(detail.getCode(), detail.getMessage()));
  }

  public record Error(String code, String message) {}
}
