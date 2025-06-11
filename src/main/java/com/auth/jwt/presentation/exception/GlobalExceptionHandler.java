package com.auth.jwt.presentation.exception;

import com.auth.jwt.global.exception.CustomException;
import com.auth.jwt.global.exception.ExceptionDetail;
import com.auth.jwt.presentation.dto.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ErrorResponse> handleAllExceptions(
      Exception exception, WebRequest request) {
    logException(exception, request);
    return buildResponse(GlobalExceptionDetail.INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(CustomException.class)
  public final ResponseEntity<ErrorResponse> handleCustomExceptions(
      CustomException exception, WebRequest request) {
    logException(exception, request);
    return buildResponse(GlobalExceptionDetail.INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private void logException(Exception exception, WebRequest request) {
    String description = request.getDescription(true);
    log.error(
        "예외 발생, {} - {}: {}",
        description,
        exception.getClass().getSimpleName(),
        exception.getMessage(),
        exception);
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      CustomException exception, HttpStatus statusCode) {
    return buildResponse(exception.getErrorDetail(), statusCode);
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      ExceptionDetail detail, HttpStatus statusCode) {
    return ResponseEntity.status(statusCode).body(ErrorResponse.from(detail));
  }
}
