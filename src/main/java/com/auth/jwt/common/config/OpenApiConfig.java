package com.auth.jwt.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "JWT 인증/인가 시스템 API",
            version = "v1.0.0",
            description =
                """
            Spring Boot 기반 JWT 인증/인가 시스템의 API 문서입니다.

            ## 주요 기능
            - 사용자 회원가입 및 로그인
            - JWT 기반 인증 토큰 발급
            - 액세스 토큰 및 리프레시 토큰 관리
            - 역할 기반 접근 제어 (RBAC)
            - 관리자 권한 부여

            ## 인증 방식
            이 API는 Bearer Token (JWT) 방식의 인증을 사용합니다.
            1. `/login` 엔드포인트로 로그인하여 토큰을 발급받습니다.
            2. 보호된 API 요청 시 `Authorization: Bearer <token>` 헤더를 포함시킵니다.
            3. 토큰이 만료되면 `/refresh-token` 엔드포인트로 토큰을 갱신합니다.

            ## 사용 방법
            1. 우상단 'Authorize' 버튼을 클릭합니다.
            2. 로그인으로 받은 JWT 토큰을 입력합니다. (Bearer 접두사 포함)
            3. 이후 모든 API 호출에 자동으로 토큰이 포함됩니다.

            ## 기본 계정 정보
            - **관리자**: username=`admin`, password=`admin123`
            - **일반 사용자**: username=`user`, password=`user1234`
            """),
    security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER,
    description =
        """
        JWT 인증을 위한 Bearer 토큰을 입력하세요.

        **형식**: Bearer <your-jwt-token>

        **예시**: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

        **토큰 획득 방법**:
        1. `/login` API로 로그인하여 토큰을 발급받습니다.
        2. 응답으로 받은 `accessToken` 값 앞에 'Bearer '를 붙여서 입력합니다.

        **주의사항**:
        - 토큰 앞에 반드시 'Bearer ' (공백 포함)를 붙여야 합니다.
        - 토큰이 만료되면 `/refresh-token` API로 갱신하세요.
        """)
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addTagsItem(new Tag().name("인증").description("로그인, 토큰 갱신 등 인증 관련 API (Filter로 처리됨)"))
        .path("/login", createLoginPath())
        .path("/refresh-token", createRefreshTokenPath());
  }

  private PathItem createLoginPath() {
    return new PathItem()
        .post(
            new Operation()
                .addTagsItem("인증")
                .summary("사용자 로그인")
                .description(
                    "사용자명과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.\n\n"
                        + "**응답 토큰**:\n"
                        + "- accessToken: API 호출에 사용하는 액세스 토큰 (만료시간: 5분)\n"
                        + "- refreshToken: 액세스 토큰 갱신에 사용하는 리프레시 토큰 (만료시간: 30일)\n\n"
                        + "**기본 계정**:\n"
                        + "- 관리자: admin / admin123\n"
                        + "- 일반 사용자: user / user1234")
                .requestBody(
                    new RequestBody()
                        .required(true)
                        .content(
                            new Content()
                                .addMediaType(
                                    "application/json",
                                    new MediaType()
                                        .addExamples(
                                            "관리자 로그인",
                                            new Example()
                                                .summary("기본 관리자 계정")
                                                .value(
                                                    Map.of(
                                                        "username", "admin",
                                                        "password", "admin123")))
                                        .addExamples(
                                            "일반 사용자 로그인",
                                            new Example()
                                                .summary("기본 일반 사용자 계정")
                                                .value(
                                                    Map.of(
                                                        "username", "user",
                                                        "password", "user1234"))))))
                .responses(
                    new ApiResponses()
                        .addApiResponse(
                            "200",
                            new ApiResponse()
                                .description("로그인 성공")
                                .content(
                                    new Content()
                                        .addMediaType(
                                            "application/json",
                                            new MediaType()
                                                .addExamples(
                                                    "성공 응답",
                                                    new Example()
                                                        .value(
                                                            Map.of(
                                                                "accessToken",
                                                                    "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfQURNSU4iLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDAzNjAwfQ.example_signature",
                                                                "refreshToken",
                                                                    "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDI1OTIwMDB9.example_signature"))))))
                        .addApiResponse(
                            "401",
                            new ApiResponse()
                                .description("로그인 실패 - 잘못된 자격 증명")
                                .content(
                                    new Content()
                                        .addMediaType(
                                            "application/json",
                                            new MediaType()
                                                .addExamples(
                                                    "로그인 실패",
                                                    new Example()
                                                        .value(
                                                            Map.of(
                                                                "error",
                                                                Map.of(
                                                                    "code",
                                                                        "UNSUCCESSFUL_AUTHENTICATION",
                                                                    "message",
                                                                        "아이디 또는 비밀번호가 올바르지 않습니다.")))))))
                        .addApiResponse(
                            "400",
                            new ApiResponse()
                                .description("잘못된 요청 형식")
                                .content(
                                    new Content()
                                        .addMediaType(
                                            "application/json",
                                            new MediaType()
                                                .addExamples(
                                                    "잘못된 형식",
                                                    new Example()
                                                        .value(
                                                            Map.of(
                                                                "error",
                                                                Map.of(
                                                                    "code", "INVALID_INPUT",
                                                                    "message",
                                                                        "입력하신 데이터에 오류가 있습니다.",
                                                                    "details",
                                                                        Map.of(
                                                                            "username",
                                                                                "아이디 입력은 필수입니다.",
                                                                            "password",
                                                                                "비밀번호 입력은 필수입니다."))))))))));
  }

  private PathItem createRefreshTokenPath() {
    return new PathItem()
        .post(
            new Operation()
                .addTagsItem("인증")
                .summary("액세스 토큰 갱신")
                .description(
                    "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.\n\n"
                        + "**사용 시기**:\n"
                        + "- 액세스 토큰이 만료되었을 때\n"
                        + "- 토큰 만료 전에 미리 갱신하고 싶을 때\n\n"
                        + "**주의사항**:\n"
                        + "- 기존 리프레시 토큰은 갱신 후 무효화됩니다.\n"
                        + "- 새로 발급받은 토큰들을 사용해야 합니다.")
                .requestBody(
                    new RequestBody()
                        .required(true)
                        .content(
                            new Content()
                                .addMediaType(
                                    "application/json",
                                    new MediaType()
                                        .addExamples(
                                            "토큰 갱신 요청",
                                            new Example()
                                                .summary("기존 토큰으로 갱신")
                                                .value(
                                                    Map.of(
                                                        "accessToken",
                                                            "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfQURNSU4iLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDAzNjAwfQ.example_signature",
                                                        "refreshToken",
                                                            "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDI1OTIwMDB9.example_signature"))))))
                .responses(
                    new ApiResponses()
                        .addApiResponse(
                            "200",
                            new ApiResponse()
                                .description("토큰 갱신 성공")
                                .content(
                                    new Content()
                                        .addMediaType(
                                            "application/json",
                                            new MediaType()
                                                .addExamples(
                                                    "성공 응답",
                                                    new Example()
                                                        .value(
                                                            Map.of(
                                                                "accessToken",
                                                                    "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfQURNSU4iLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTcwMDAxMDgwMCwiZXhwIjoxNzAwMDE0NDAwfQ.new_signature",
                                                                "refreshToken",
                                                                    "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzAwMDEwODAwLCJleHAiOjE3MDI2MDI4MDB9.new_signature"))))))
                        .addApiResponse(
                            "401",
                            new ApiResponse()
                                .description("토큰 갱신 실패")
                                .content(
                                    new Content()
                                        .addMediaType(
                                            "application/json",
                                            new MediaType()
                                                .addExamples(
                                                    "유효하지 않은 리프레시 토큰",
                                                    new Example()
                                                        .summary("리프레시 토큰이 유효하지 않음")
                                                        .value(
                                                            Map.of(
                                                                "error",
                                                                Map.of(
                                                                    "code", "INVALID_REFRESH_TOKEN",
                                                                    "message",
                                                                        "유효하지 않은 리프레시 토큰입니다."))))
                                                .addExamples(
                                                    "만료된 토큰",
                                                    new Example()
                                                        .summary("토큰이 만료됨")
                                                        .value(
                                                            Map.of(
                                                                "error",
                                                                Map.of(
                                                                    "code", "TOKEN_EXPIRED",
                                                                    "message", "토큰이 만료되었습니다."))))
                                                .addExamples(
                                                    "등록되지 않은 토큰",
                                                    new Example()
                                                        .summary("저장소에 없는 토큰")
                                                        .value(
                                                            Map.of(
                                                                "error",
                                                                Map.of(
                                                                    "code", "INVALID_REFRESH_TOKEN",
                                                                    "message",
                                                                        "등록되지 않은 리프레시 토큰입니다.")))))))
                        .addApiResponse(
                            "400",
                            new ApiResponse()
                                .description("잘못된 요청 형식")
                                .content(
                                    new Content()
                                        .addMediaType(
                                            "application/json",
                                            new MediaType()
                                                .addExamples(
                                                    "잘못된 JSON 형식",
                                                    new Example()
                                                        .value(
                                                            Map.of(
                                                                "error",
                                                                Map.of(
                                                                    "code", "NOT_READABLE",
                                                                    "message",
                                                                        "입력하신 데이터가 잘못된 형식입니다.")))))))));
  }
}
