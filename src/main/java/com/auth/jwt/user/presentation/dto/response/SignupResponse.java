package com.auth.jwt.user.presentation.dto.response;

import com.auth.jwt.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답 정보")
public record SignupResponse(
    @Schema(description = "생성된 사용자 ID", example = "1") Long userId,
    @Schema(description = "사용자 아이디", example = "testuser") String username,
    @Schema(description = "사용자 닉네임", example = "테스트유저") String nickname,
    @Schema(
            description = "사용자 역할",
            example = "일반 사용자",
            allowableValues = {"일반 사용자", "관리자"})
        String role) {
  public static SignupResponse from(User user) {
    return new SignupResponse(
        user.getId().getValue(),
        user.getUsername().getValue(),
        user.getNickname().getValue(),
        user.getRole().getDescription());
  }
}
