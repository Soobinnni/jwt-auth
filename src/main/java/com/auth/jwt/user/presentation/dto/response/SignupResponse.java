package com.auth.jwt.user.presentation.dto.response;

import com.auth.jwt.user.domain.entity.User;
import java.util.List;

public record SignupResponse(Long userId, String username, String nickname, List<String> roles) {
  public static SignupResponse from(User user) {
    return new SignupResponse(
        user.getId().getValue(),
        user.getUsername().getValue(),
        user.getNickname().getValue(),
        user.getRoles().stream().map(role -> role.getDescription()).toList());
  }
}
