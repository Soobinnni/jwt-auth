package com.auth.jwt.presentation.exception;

import com.auth.jwt.global.exception.ExceptionDetail;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionDetail implements ExceptionDetail {
  INTERNAL("INTERNAL_EXCEPTION", "내부 오류가 발생하였습니니다.");

  private final String code;
  private final String message;
}
