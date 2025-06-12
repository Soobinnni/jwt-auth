package com.auth.jwt.auth.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 갱신 응답 정보")
public record RefreshTokenResponse(
    @Schema(
            description = "새로운 액세스 토큰",
            example =
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsInVzZXJuYW1lIjoidXNlciIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3MDAwMTA4MDAsImV4cCI6MTcwMDAxNDQwMH0.new_signature")
        String accessToken,
    @Schema(
            description = "새로운 리프레시 토큰",
            example =
                "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6InVzZXIiLCJpYXQiOjE3MDAwMTA4MDAsImV4cCI6MTcwMjYwMjgwMH0.new_signature")
        String refreshToken) {}
