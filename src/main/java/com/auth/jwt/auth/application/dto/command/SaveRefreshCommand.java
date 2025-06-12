package com.auth.jwt.auth.application.dto.command;

public record SaveRefreshCommand(Long userId, String refreshToken) {}
