package com.auth.jwt.auth.presentation.dto.request;

import com.auth.jwt.auth.application.dto.command.CredentialCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "아이디 입력은 필수입니다.") String username,
    @NotBlank(message = "비밀번호 입력은 필수입니다.") String password) {
  public CredentialCommand toInfo() {
    return new CredentialCommand(username, password);
  }
}
