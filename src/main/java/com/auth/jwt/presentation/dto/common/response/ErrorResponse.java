package com.auth.jwt.presentation.dto.common.response;

import com.auth.jwt.global.exception.ExceptionDetail;

public record ErrorResponse(Error error) {
  public static ErrorResponse from(ExceptionDetail detail) {
    return new ErrorResponse(new Error(detail.getCode(), detail.getMessage()));
  }

  public record Error(String code, String message) {}
}
