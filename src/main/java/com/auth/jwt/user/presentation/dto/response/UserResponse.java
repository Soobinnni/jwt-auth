package com.auth.jwt.user.presentation.dto.response;

import com.auth.jwt.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 정보 응답")
public record UserResponse(
    @Schema(description = "사용자 ID", example = "2") Long userId,
    @Schema(description = "사용자 아이디", example = "targetuser") String username,
    @Schema(description = "사용자 닉네임", example = "대상유저") String nickname,
    @Schema(
            description = "사용자 역할",
            example = "관리자",
            allowableValues = {"일반 사용자", "관리자"})
        String role) {
  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId().getValue(),
        user.getUsername().getValue(),
        user.getNickname().getValue(),
        user.getRole().getDescription());
  }
}
