package com.auth.jwt.auth.application.dto.result;

public record CreateTokenPairResult(String accessToken, String refreshToken) {}
