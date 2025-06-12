package com.auth.jwt.user.application.dto.command;

public record RoleGrantCommand(Long userId) {
  public static RoleGrantCommand of(Long userId) {
    return new RoleGrantCommand(userId);
  }
}
