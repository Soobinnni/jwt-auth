package com.auth.jwt.auth.application.dto.result;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenValidationResult {
  private Long userId;
  private boolean isValid;
  private String code;
  private String message;

  public static TokenValidationResult success(Long userId) {
    return new TokenValidationResult(userId, true, null, null);
  }

  public static TokenValidationResult fail(String code, String message) {
    return new TokenValidationResult(null, false, code, message);
  }
}
