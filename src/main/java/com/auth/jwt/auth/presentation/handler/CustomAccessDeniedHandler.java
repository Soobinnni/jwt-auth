package com.auth.jwt.auth.presentation.handler;

import com.auth.jwt.auth.presentation.utils.AuthResponseSender;
import com.auth.jwt.common.model.CustomPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final AuthResponseSender authResponseSender;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {

    log.warn(
        "접근 거부 - URI: {}, 사용자: {}, 사유: {}",
        request.getRequestURI(),
        getCurrentUsername(),
        accessDeniedException.getMessage());

    authResponseSender.sendErrorResponse(
        request,
        response,
        HttpServletResponse.SC_FORBIDDEN,
        "ACCESS_DENIED",
        "권한이 필요한 요청입니다. 접근 권한이 없습니다.",
        null);
  }

  private String getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.getPrincipal() instanceof CustomPrincipal principal) {
      return "User ID: " + principal.id();
    }
    return "Anonymous";
  }
}
