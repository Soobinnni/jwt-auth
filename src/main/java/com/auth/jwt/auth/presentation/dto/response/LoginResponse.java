package com.auth.jwt.auth.presentation.dto.response;

import com.auth.jwt.auth.application.dto.result.CreateTokenPairResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답 정보")
public record LoginResponse(
    @Schema(
            description = "액세스 토큰 (API 호출에 사용, 만료시간: 5분)",
            example =
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsInVzZXJuYW1lIjoidXNlciIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDAwMzYwMH0.signature")
        String accessToken,
    @Schema(
            description = "리프레시 토큰 (토큰 갱신에 사용, 만료시간: 30일)",
            example =
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6InVzZXIiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMjU5MjAwMH0.signature")
        String refreshToken) {
  public static LoginResponse from(CreateTokenPairResult result) {
    return new LoginResponse(result.accessToken(), result.refreshToken());
  }
}
