package com.auth.jwt.application.dto.auth;

public record SignupCommand(String username, String password, String nickname) {}
