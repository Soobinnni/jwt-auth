package com.auth.jwt.auth.presentation.filter;

import com.auth.jwt.auth.application.AuthenticationService;
import com.auth.jwt.auth.application.dto.command.SaveRefreshCommand;
import com.auth.jwt.auth.application.dto.result.AuthenticationResult;
import com.auth.jwt.auth.application.dto.result.CreateTokenPairResult;
import com.auth.jwt.auth.presentation.dto.request.LoginRequest;
import com.auth.jwt.auth.presentation.dto.response.LoginResponse;
import com.auth.jwt.auth.presentation.utils.AuthResponseSender;
import com.auth.jwt.common.model.CustomPrincipal;
import com.auth.jwt.common.utils.LoggingUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationService authenticationService;
  private final AuthResponseSender authResponseSender;
  private final ObjectMapper objectMapper;

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      String requestBody = getRequestBody(request);

      LoginRequest loginRequest = parseLoginRequest(requestBody);

      if (log.isDebugEnabled()) {
        log.debug(
            "로그인 시도 - 아이디: {}, IP: {}", loginRequest.username(), LoggingUtil.getClientIp(request));
      }

      AuthenticationResult result =
          authenticationService.getUserByCredentialInfo(loginRequest.toInfo());
      CustomPrincipal principal = new CustomPrincipal(result.id(), result.role());

      return new UsernamePasswordAuthenticationToken(
          principal, null, extractAuthorities(principal.role()));
    } catch (com.auth.jwt.auth.application.exception.AuthenticationException e) {
      log.error("자격 증명 중 오류: {}", e.getMessage());
      throw new AuthenticationException(e.getMessage()) {};
    } catch (IOException e) {
      log.error("로그인 요청 처리 중 IO 오류: {}", e.getMessage());
      throw new AuthenticationException("로그인 요청을 처리할 수 없습니다.") {};
    } catch (IllegalArgumentException e) {
      log.error("유효성 검증 오류: {}", e.getMessage());
      throw new AuthenticationException(e.getMessage()) {};
    }
  }

  private String getRequestBody(HttpServletRequest request) throws IOException {
    try (BufferedReader reader = request.getReader()) {
      return reader.lines().collect(Collectors.joining("\n"));
    }
  }

  private LoginRequest parseLoginRequest(String requestBody) throws IOException {
    if (requestBody == null || requestBody.trim().isEmpty()) {
      log.warn("빈 요청 본문 수신");
      return LoginRequest.empty();
    }

    try {
      return objectMapper.readValue(requestBody, LoginRequest.class);
    } catch (JsonProcessingException e) {
      log.error("JSON 파싱 오류: {}", e.getMessage());
      throw new IOException("잘못된 JSON 형식입니다.", e);
    }
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse,
      FilterChain chain,
      Authentication authResult)
      throws AuthenticationException {

    try {
      CustomPrincipal principal = (CustomPrincipal) authResult.getPrincipal();
      Long userId = principal.id();

      CreateTokenPairResult result = authenticationService.createAuthenticationToken(userId);

      authenticationService.saveRefreshToken(new SaveRefreshCommand(userId, result.refreshToken()));

      LoginResponse response = LoginResponse.from(result);
      authResponseSender.sendSuccessResponse(httpResponse, response);

      log.info("로그인 성공 - 사용자: {}, IP: {}", userId, LoggingUtil.getClientIp(httpRequest));
    } catch (Exception e) {
      log.error("인증 완료 후 처리 중 오류: {}", e.getMessage());
      throw new AuthenticationException("로그인 처리 중 오류가 발생했습니다.") {};
    }
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    log.info("로그인 실패 - 원인: {}, IP: {}", failed.getMessage(), LoggingUtil.getClientIp(request));

    authResponseSender.sendErrorResponse(
        request,
        response,
        HttpServletResponse.SC_UNAUTHORIZED,
        "UNSUCCESSFUL_AUTHENTICATION",
        failed.getMessage(),
        null);
  }

  private List<GrantedAuthority> extractAuthorities(String role) {
    return List.of(new SimpleGrantedAuthority(role));
  }
}
