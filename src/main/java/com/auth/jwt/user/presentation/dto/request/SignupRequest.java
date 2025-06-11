package com.auth.jwt.user.presentation.dto.request;

import com.auth.jwt.user.application.dto.command.SignupCommand;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
    @NotBlank(message = "아이디는 필수입니다.") String username,
    @NotBlank(message = "비밀번호는 필수입니다.") String password,
    @NotBlank(message = "닉네임은 필수입니다.") String nickname) {
  public SignupCommand toCommand() {
    return new SignupCommand(username, password, nickname);
  }
}
