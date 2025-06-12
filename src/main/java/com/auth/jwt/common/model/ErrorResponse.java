package com.auth.jwt.common.model;

import com.auth.jwt.common.exception.ExceptionDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "에러 응답 정보")
public record ErrorResponse(@Schema(description = "에러 정보") Error error) {
  public static ErrorResponse from(ExceptionDetail detail) {
    return new ErrorResponse(new Error(detail.getCode(), detail.getMessage(), null));
  }

  public static ErrorResponse from(ExceptionDetail detail, Map<String, String> details) {
    return new ErrorResponse(new Error(detail.getCode(), detail.getMessage(), details));
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Schema(description = "에러 상세 정보")
  public record Error(
      @Schema(description = "에러 코드", example = "USER_ALREADY_EXISTS") String code,
      @Schema(description = "에러 메시지", example = "이미 가입된 사용자입니다.") String message,
      @Schema(
              description = "상세 에러 정보 (입력 검증 실패 시에만 제공)",
              example =
                  """
          {
            "username": "아이디는 필수입니다.",
            "password": "비밀번호는 필수입니다."
          }
          """)
          Map<String, String> details) {}
}
