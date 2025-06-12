package com.auth.jwt.user.presentation;

import com.auth.jwt.user.application.UserCommandService;
import com.auth.jwt.user.application.dto.command.RoleGrantCommand;
import com.auth.jwt.user.presentation.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Tag(name = "관리자 전용", description = "관리자만 접근 가능한 사용자 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {
  private final UserCommandService userCommandService;

  @Operation(
      summary = "사용자에게 관리자 권한 부여",
      description =
          """
          지정된 사용자에게 관리자 권한을 부여합니다.

          **권한 요구사항**: 관리자(ADMIN) 권한 필요

          **주의사항**:
          - 관리자만 이 API를 호출할 수 있습니다.
          """)
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "관리자 권한 부여 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples =
                        @ExampleObject(
                            name = "성공 응답",
                            value =
                                """
                      {
                        "userId": 2,
                        "username": "targetuser",
                        "nickname": "대상유저",
                        "role": "관리자"
                      }
                      """))),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 - 관리자 권한 필요",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            name = "권한 부족",
                            value =
                                """
                      {
                        "error": {
                          "code": "ACCESS_DENIED",
                          "message": "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."
                        }
                      }
                      """))),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 사용자",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            name = "사용자 없음",
                            value =
                                """
                      {
                        "error": {
                          "code": "NOT_FOUND_USER",
                          "message": "사용자를 찾을 수 없습니다."
                        }
                      }
                      """))),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - 유효하지 않은 토큰",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            name = "인증 실패",
                            value =
                                """
                      {
                        "error": {
                          "code": "INVALID_TOKEN",
                          "message": "유효하지 않은 토큰입니다."
                        }
                      }
                      """)))
      })
  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{userId}/roles")
  public ResponseEntity<UserResponse> grantAdminRole(
      @Parameter(description = "관리자 권한을 부여할 사용자의 ID", required = true, example = "2") @PathVariable
          Long userId) {
    RoleGrantCommand command = new RoleGrantCommand(userId);
    UserResponse response = UserResponse.from(userCommandService.grantAdminRole(command));

    return ResponseEntity.ok(response);
  }
}
