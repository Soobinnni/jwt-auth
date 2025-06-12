package com.auth.jwt.user.presentation.dto.request;

import com.auth.jwt.user.application.dto.command.SignupCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원가입 요청 정보")
public record SignupRequest(
    @Schema(
            description = "사용자 아이디",
            example = "testuser",
            minLength = 2,
            maxLength = 50,
            required = true)
        @NotBlank(message = "아이디는 필수입니다.")
        String username,
    @Schema(
            description = "비밀번호 (영문자+숫자 조합, 8-20자)",
            example = "password123",
            minLength = 8,
            maxLength = 20,
            required = true)
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,
    @Schema(
            description = "사용자 닉네임",
            example = "테스트유저",
            minLength = 1,
            maxLength = 30,
            required = true)
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname) {
  public SignupCommand toCommand() {
    return new SignupCommand(username, password, nickname);
  }
}
