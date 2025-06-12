package com.auth.jwt.user.presentation.dto.response;

import com.auth.jwt.user.domain.entity.User;

public record UserResponse(Long userId, String username, String nickname, String role) {
  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId().getValue(),
        user.getUsername().getValue(),
        user.getNickname().getValue(),
        user.getRole().getDescription());
  }
}
