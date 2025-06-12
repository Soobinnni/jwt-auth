package com.auth.jwt.auth.presentation.dto.response;


public record RefreshTokenResponse(String accessToken, String refreshToken) {}
