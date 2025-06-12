package com.auth.jwt.auth.application.dto.result;

public record TokenReissueResult(
    TokenValidationResult validation, CreateTokenPairResult tokenPair) {
  public static TokenReissueResult success(Long userId, CreateTokenPairResult tokenPair) {
    return new TokenReissueResult(TokenValidationResult.success(userId), tokenPair);
  }

  public static TokenReissueResult fail(String code, String message) {
    return new TokenReissueResult(TokenValidationResult.fail(code, message), null);
  }
}
