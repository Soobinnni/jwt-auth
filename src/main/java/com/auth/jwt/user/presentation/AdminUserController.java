package com.auth.jwt.user.presentation;

import com.auth.jwt.user.application.UserCommandService;
import com.auth.jwt.user.application.dto.command.RoleGrantCommand;
import com.auth.jwt.user.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {
  private final UserCommandService userCommandService;

  @PatchMapping("/{userId}/roles")
  public ResponseEntity<UserResponse> grantAdminRole(@PathVariable Long userId) {
    RoleGrantCommand command = RoleGrantCommand.of(userId);
    UserResponse response = UserResponse.from(userCommandService.grantAdminRole(command));

    return ResponseEntity.ok(response);
  }
}
