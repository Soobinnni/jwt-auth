package com.auth.jwt.auth.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
    @NotNull(message = "access token이 필요합니다.") String accessToken,
    @NotNull(message = "refresh token이 필요합니다.") String refreshToken) {}
