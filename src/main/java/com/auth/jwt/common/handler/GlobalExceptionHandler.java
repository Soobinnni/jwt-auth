package com.auth.jwt.common.handler;

import com.auth.jwt.common.dto.ErrorResponse;
import com.auth.jwt.common.exception.*;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InvalidException.class)
  public final ResponseEntity<ErrorResponse> handleInvalidExceptions(
      InvalidException exception, WebRequest request) {
    logException(exception, request);
    return ResponseBuilder.build(exception, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public final ResponseEntity<ErrorResponse> handleDuplicateResourceExceptions(
      DuplicateResourceException exception, WebRequest request) {
    logException(exception, request);
    return ResponseBuilder.build(exception, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BusinessException.class)
  public final ResponseEntity<ErrorResponse> handleBusinessExceptions(
      BusinessException exception, WebRequest request) {
    logException(exception, request);
    return ResponseBuilder.build(exception, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    logException(exception, request);
    return ResponseBuilder.build(
        GlobalExceptionDetail.INVALID_INPUT,
        ResponseBuilder.extractFieldErrors(exception),
        HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    logException(exception, request);
    return ResponseBuilder.build(
        GlobalExceptionDetail.NOT_READABLE,
        Map.of("message", exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    logException(exception, request);
    return ResponseBuilder.build(
        GlobalExceptionDetail.INVALID_REQUEST,
        ResponseBuilder.extractMethodErrors(exception),
        HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    logException(exception, request);
    return ResponseBuilder.build(
        GlobalExceptionDetail.NOT_ALLOWED,
        Map.of("method", exception.getMethod()),
        HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest request) {
    logException(exception, request);
    return ResponseBuilder.build(GlobalExceptionDetail.INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR);
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

  @Getter
  @RequiredArgsConstructor
  enum GlobalExceptionDetail implements ExceptionDetail {
    INTERNAL("내부 오류가 발생하였습니다."),
    INVALID_INPUT("입력하신 데이터에 오류가 있습니다. 요청 내용을 확인하고 다시 시도해 주세요."),
    NOT_READABLE("입력하신 데이터가 잘못된 형식입니다."),
    INVALID_REQUEST("요청하신 값이 올바르지 않습니다. 요청 값을 확인해 주세요."),
    NOT_ALLOWED("허용되지 않은 HTTP 메서드입니다.");

    private final String message;
    private final String code = name();
  }

  private static class ResponseBuilder {

    static ResponseEntity<ErrorResponse> build(CustomException exception, HttpStatus statusCode) {
      return ResponseEntity.status(statusCode)
          .body(ErrorResponse.from(exception.getExceptionDetail(), null));
    }

    static ResponseEntity<Object> build(ExceptionDetail content, HttpStatus statusCode) {
      return ResponseEntity.status(statusCode).body(ErrorResponse.from(content, null));
    }

    static ResponseEntity<Object> build(
        ExceptionDetail content, Map<String, String> details, HttpStatus statusCode) {
      return ResponseEntity.status(statusCode).body(ErrorResponse.from(content, details));
    }

    static Map<String, String> extractFieldErrors(MethodArgumentNotValidException ex) {
      return ex.getBindingResult().getFieldErrors().stream()
          .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    static Map<String, String> extractMethodErrors(HandlerMethodValidationException ex) {
      return ex.getParameterValidationResults().stream()
          .collect(
              Collectors.toMap(
                  result -> {
                    MessageSourceResolvable error = result.getResolvableErrors().get(0);
                    return error.getCodes()[1];
                  },
                  result -> result.getResolvableErrors().get(0).getDefaultMessage(),
                  (existing, replacement) -> existing));
    }
  }
}
