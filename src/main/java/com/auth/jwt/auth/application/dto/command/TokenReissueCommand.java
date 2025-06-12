package com.auth.jwt.auth.application.dto.command;

public record TokenReissueCommand(String accessToken, String refreshToken) {}
