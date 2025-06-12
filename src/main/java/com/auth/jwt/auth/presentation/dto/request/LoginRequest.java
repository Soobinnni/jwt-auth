package com.auth.jwt.auth.presentation.dto.request;

import com.auth.jwt.auth.application.dto.command.CredentialCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 정보")
public record LoginRequest(
    @Schema(description = "사용자 아이디", example = "admin", required = true)
        @NotBlank(message = "아이디 입력은 필수입니다.")
        String username,
    @Schema(description = "비밀번호", example = "admin123", required = true)
        @NotBlank(message = "비밀번호 입력은 필수입니다.")
        String password) {
  public CredentialCommand toInfo() {
    return new CredentialCommand(username, password);
  }
}
