package com.auth.jwt.auth.presentation.filter;

import com.auth.jwt.auth.application.AuthenticationService;
import com.auth.jwt.auth.application.dto.result.TokenReissueResult;
import com.auth.jwt.auth.presentation.dto.request.TokenReissueRequest;
import com.auth.jwt.auth.presentation.dto.response.RefreshTokenResponse;
import com.auth.jwt.auth.presentation.utils.AuthResponseSender;
import com.auth.jwt.common.utils.LoggingUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {
  private static final String REFRESH_URI = "/refresh-token";
  private final AuthenticationService authenticationService;
  private final AuthResponseSender authResponseSender;
  private final ObjectMapper objectMapper;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().equals(REFRESH_URI);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String requestBody = getRequestBody(httpRequest);
      TokenReissueRequest refreshRequest = parseTokenReissueRequest(requestBody);

      if (log.isDebugEnabled()) {
        log.debug("토큰 갱신 요청 - IP: {}", LoggingUtil.getClientIp(httpRequest));
      }

      TokenReissueResult result =
          authenticationService.reIssueTokenPair(refreshRequest.toCommand());

      if (!result.validation().isValid()) {
        Map<String, String> details = new HashMap<>(Map.of("code", result.validation().getCode()));
        unsuccessfulAuthentication(
            httpRequest,
            httpResponse,
            result.validation().getCode(),
            result.validation().getMessage(),
            details);
        return;
      }

      successfulAuthentication(httpRequest, httpResponse, result);

    } catch (IllegalArgumentException e) {
      log.error("토큰 갱신 요청 유효성 검증 오류: {}", e.getMessage());
      unsuccessfulAuthentication(httpRequest, httpResponse, e, null);
    } catch (IOException e) {
      log.error("토큰 갱신 요청 처리 중 IO 오류: {}", e.getMessage());
      unsuccessfulAuthentication(
          httpRequest, httpResponse, new Exception("토큰 갱신 요청을 처리할 수 없습니다."), null);
    } catch (Exception e) {
      log.warn(
          "토큰 갱신 요청 처리 중 오류: {}, IP: {}", e.getMessage(), LoggingUtil.getClientIp(httpRequest));
      unsuccessfulAuthentication(httpRequest, httpResponse, e, null);
    }
  }

  private String getRequestBody(HttpServletRequest request) throws IOException {
    try (BufferedReader reader = request.getReader()) {
      return reader.lines().collect(Collectors.joining("\n"));
    }
  }

  private TokenReissueRequest parseTokenReissueRequest(String requestBody) throws IOException {
    if (requestBody == null || requestBody.trim().isEmpty()) {
      log.warn("토큰 갱신 - 빈 요청 본문 수신");
      return TokenReissueRequest.empty();
    }

    try {
      return objectMapper.readValue(requestBody, TokenReissueRequest.class);
    } catch (JsonProcessingException e) {
      log.error("토큰 갱신 - JSON 파싱 오류: {}", e.getMessage());
      throw new IOException("잘못된 JSON 형식입니다.", e);
    }
  }

  protected void successfulAuthentication(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse, TokenReissueResult result) {
    try {
      RefreshTokenResponse response =
          new RefreshTokenResponse(
              result.tokenPair().accessToken(), result.tokenPair().refreshToken());
      authResponseSender.sendSuccessResponse(httpResponse, response);

      log.info(
          "토큰 갱신 성공 - 사용자: {}, IP: {}",
          result.validation().getUserId(),
          LoggingUtil.getClientIp(httpRequest));
    } catch (AuthenticationException authException) {
      log.error("토큰 갱신 후 인증 정보 조회 오류: {}", authException.getMessage());
      throw authException;
    } catch (Exception e) {
      log.error("토큰 갱신 후 응답 생성 중 오류: {}", e.getMessage());
      throw e;
    }
  }

  protected void unsuccessfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      Exception exception,
      Map<String, String> details) {
    log.warn("토큰 갱신 실패 - 원인: {}, IP: {}", exception.getMessage(), LoggingUtil.getClientIp(request));
    authResponseSender.sendErrorResponse(
        request,
        response,
        HttpServletResponse.SC_UNAUTHORIZED,
        "TOKEN_REISSUE_FAILED",
        exception.getMessage(),
        details);
  }

  protected void unsuccessfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      String code,
      String message,
      Map<String, String> details) {
    log.warn("토큰 갱신 실패 - 메시지: {}, IP: {}", message, LoggingUtil.getClientIp(request));
    authResponseSender.sendErrorResponse(
        request, response, HttpServletResponse.SC_UNAUTHORIZED, code, message, details);
  }
}
