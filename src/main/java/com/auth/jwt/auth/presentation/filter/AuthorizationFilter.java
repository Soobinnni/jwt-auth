package com.auth.jwt.auth.presentation.filter;

import com.auth.jwt.auth.application.AuthorizationService;
import com.auth.jwt.auth.application.dto.result.TokenValidationResult;
import com.auth.jwt.auth.presentation.utils.AuthResponseSender;
import com.auth.jwt.common.model.CustomPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthorizationService authorizationService;
  private final AuthResponseSender authResponseSender;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    String method = request.getMethod();
    return (path.startsWith("/swagger-ui"))
        || (path.startsWith("/v3/api-docs"))
        || (path.equals("/swagger-ui.html"))
        || (path.equals("/users") && method.equals("POST"))
        || (path.equals("/errors"));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    try {
      String token = extractTokenFromHeader(request);

      if (token != null) {
        TokenValidationResult result = authorizationService.validateAccessToken(token);
        if (!result.isValid()) {
          Map<String, String> details = new HashMap<>(Map.of("code", result.getCode()));
          handleAuthorizationFailure(
              request, response, result.getCode(), result.getMessage(), details);
          return;
        }
        setAuthentication(result.getUserId());
      } else {
        log.warn("원인: 토큰이 존재하지 않음, 토큰: {}", token);
        handleAuthorizationFailure(request, response, "INVALID_TOKEN", "유효하지 않은 토큰 값입니다.", null);
        return;
      }

      filterChain.doFilter(request, response);
    } catch (Exception e) {
      log.error("접근 권한 확인 중 오류 발생: ", e);
      handleAuthorizationFailure(request, response, "AUTHORIZATION_FAILED", e.getMessage(), null);
    }
  }

  private String extractTokenFromHeader(HttpServletRequest request) {
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      return authHeader.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  private void handleAuthorizationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      String code,
      String message,
      Map<String, String> details) {
    authResponseSender.sendErrorResponse(
        request, response, HttpServletResponse.SC_FORBIDDEN, code, message, details);
  }

  public void setAuthentication(Long useId) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = createAuthentication(useId);
    if (authentication == null) {
      throw new AccessDeniedException("잘못된 컨텍스트 저장입니다.");
    }
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
  }

  private Authentication createAuthentication(Long userId) {
    CustomPrincipal principal = authorizationService.getPrincipal(userId);
    return new UsernamePasswordAuthenticationToken(
        principal, null, extractAuthorities(principal.role()));
  }

  private List<GrantedAuthority> extractAuthorities(String role) {
    return List.of(new SimpleGrantedAuthority(role));
  }
}
