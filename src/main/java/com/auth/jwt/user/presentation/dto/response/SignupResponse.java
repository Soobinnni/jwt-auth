package com.auth.jwt.user.presentation.dto.response;

import com.auth.jwt.user.domain.entity.User;

public record SignupResponse(Long userId, String username, String nickname, String role) {
  public static SignupResponse from(User user) {
    return new SignupResponse(
        user.getId().getValue(),
        user.getUsername().getValue(),
        user.getNickname().getValue(),
        user.getRole().getDescription());
  }
}
