package com.auth.jwt.auth.presentation.dto.response;

import com.auth.jwt.auth.application.dto.result.CreateTokenPairResult;

public record LoginResponse(String accessToken, String refreshToken) {
  public static LoginResponse from(CreateTokenPairResult result) {
    return new LoginResponse(result.accessToken(), result.refreshToken());
  }
}
