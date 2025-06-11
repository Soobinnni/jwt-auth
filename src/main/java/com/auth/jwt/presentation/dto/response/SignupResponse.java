package com.auth.jwt.presentation.dto.response;

import com.auth.jwt.application.dto.user.UserDto;
import com.auth.jwt.domain.user.User;
import java.util.List;

public record SignupResponse(
    Long userId, String username, String nickname, List<RoleResponse> roles) {
  public static SignupResponse from(User userDto) {
    return new SignupResponse(
        userDto.getUsername(),
        userDto.getNickname(),
        userDto.getRoles().stream().map(RoleResponse::from).toList());
  }

  public record RoleResponse(String role) {
    public static RoleResponse from(UserDto.RoleDto roleDto) {
      String roleWithoutPrefix =
          roleDto.getRole().startsWith("ROLE_")
              ? roleDto.getRole().substring(5)
              : roleDto.getRole();
      return new RoleResponse(roleWithoutPrefix);
    }
  }
}
