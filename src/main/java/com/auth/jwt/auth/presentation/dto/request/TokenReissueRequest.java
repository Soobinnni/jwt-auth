package com.auth.jwt.auth.presentation.dto.request;

import com.auth.jwt.auth.application.dto.command.TokenReissueCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 갱신 요청 정보")
public record TokenReissueRequest(
    @Schema(
            description = "현재 액세스 토큰 (만료되었을 수도 있음)",
            example =
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsInVzZXJuYW1lIjoidXNlciIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDAwMzYwMH0.signature",
            required = true)
        @NotBlank(message = "액세스 토큰은 필수입니다.")
        String accessToken,
    @Schema(
            description = "리프레시 토큰",
            example =
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6InVzZXIiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMjU5MjAwMH0.signature",
            required = true)
        @NotBlank(message = "리프레시 토큰은 필수입니다.")
        String refreshToken) {
  public static TokenReissueRequest empty() {
    return new TokenReissueRequest("", "");
  }

  public boolean isEmpty() {
    return (accessToken == null || accessToken.trim().isEmpty())
        && (refreshToken == null || refreshToken.trim().isEmpty());
  }

  public void validateOrThrow() {
    if (isEmpty()) {
      throw new IllegalArgumentException("토큰 정보가 비어있습니다.");
    }
    if (accessToken == null || accessToken.trim().isEmpty()) {
      throw new IllegalArgumentException("액세스 토큰은 필수입니다.");
    }
    if (refreshToken == null || refreshToken.trim().isEmpty()) {
      throw new IllegalArgumentException("리프레시 토큰은 필수입니다.");
    }
  }

  public TokenReissueCommand toCommand() {
    validateOrThrow();
    return new TokenReissueCommand(accessToken, refreshToken);
  }
}
