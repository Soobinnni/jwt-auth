package com.auth.jwt.user.presentation;

import com.auth.jwt.user.application.UserCommandService;
import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.presentation.dto.request.SignupRequest;
import com.auth.jwt.user.presentation.dto.response.SignupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "사용자 관리", description = "사용자 회원가입 및 계정 관리 API")
public class UserController {
  private final UserCommandService userCommandService;

  @Operation(
      summary = "사용자 회원가입",
      description =
          """
          새로운 사용자 계정을 생성합니다.

          **입력 규칙**:
          - 아이디: 2-50자, 필수
          - 비밀번호: 8-20자, 영문자+숫자 조합, 필수
          - 닉네임: 1-30자, 필수

          **참고**: 이 API는 인증이 필요하지 않습니다.
          """,
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "회원가입 정보",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = SignupRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "일반 사용자 예시",
                            description = "일반 사용자 회원가입 예시",
                            value =
                                """
                          {
                            "username": "testuser",
                            "password": "password123",
                            "nickname": "테스트유저"
                          }
                          """),
                        @ExampleObject(
                            name = "관리자 예시",
                            description = "관리자 계정 생성 예시",
                            value =
                                """
                          {
                            "username": "admin2",
                            "password": "admin1234",
                            "nickname": "관리자2"
                          }
                          """)
                      })))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "회원가입 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SignupResponse.class),
                    examples =
                        @ExampleObject(
                            name = "성공 응답",
                            value =
                                """
                      {
                        "userId": 1,
                        "username": "testuser",
                        "nickname": "테스트유저",
                        "role": "일반 사용자"
                      }
                      """)),
            headers =
                @io.swagger.v3.oas.annotations.headers.Header(
                    name = "Location",
                    description = "생성된 사용자 리소스의 URI",
                    schema = @Schema(type = "string", example = "/users/1"))),
        @ApiResponse(
            responseCode = "400",
            description = "입력 데이터 유효성 검증 실패",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          name = "필수 필드 누락",
                          value =
                              """
                          {
                            "error": {
                              "code": "INVALID_INPUT",
                              "message": "입력하신 데이터에 오류가 있습니다. 요청 내용을 확인하고 다시 시도해 주세요.",
                              "details": {
                                "username": "아이디는 필수입니다.",
                                "password": "비밀번호는 필수입니다."
                              }
                            }
                          }
                          """),
                      @ExampleObject(
                          name = "비밀번호 형식 오류",
                          value =
                              """
                          {
                            "error": {
                              "code": "INVALID_LENGTH_PASSWORD",
                              "message": "비밀번호가 최소 8 길이와, 최대 20 길이여야 합니다."
                            }
                          }
                          """)
                    })),
        @ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 사용자명",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            name = "중복 사용자명",
                            value =
                                """
                      {
                        "error": {
                          "code": "USER_ALREADY_EXISTS",
                          "message": "이미 가입된 사용자입니다."
                        }
                      }
                      """)))
      })
  @SecurityRequirements(value = {}) // 이 API는 인증이 필요하지 않음
  @PostMapping
  public ResponseEntity<SignupResponse> signup(
      @Parameter(description = "회원가입 요청 정보", required = true) @Valid @RequestBody
          SignupRequest request) {
    User user = userCommandService.signup(request.toCommand());
    SignupResponse response = SignupResponse.from(user);

    return ResponseEntity.created(buildResourceLocation(response.userId())).body(response);
  }

  private URI buildResourceLocation(Long userId) {
    return URI.create("/users/" + userId);
  }
}
