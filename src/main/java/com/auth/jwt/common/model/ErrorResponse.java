package com.auth.jwt.common.model;

import com.auth.jwt.common.exception.ExceptionDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

public record ErrorResponse(Error error) {
  public static ErrorResponse from(ExceptionDetail detail) {
    return new ErrorResponse(new Error(detail.getCode(), detail.getMessage(), null));
  }

  public static ErrorResponse from(ExceptionDetail detail, Map<String, String> details) {
    return new ErrorResponse(new Error(detail.getCode(), detail.getMessage(), details));
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record Error(String code, String message, Map<String, String> details) {}
}
