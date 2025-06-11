package com.auth.jwt.user.application.dto.command;

public record SignupCommand(String username, String password, String nickname) {}
