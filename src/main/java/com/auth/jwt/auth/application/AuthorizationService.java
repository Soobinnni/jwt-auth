package com.auth.jwt.auth.application;

import com.auth.jwt.auth.application.dto.result.TokenValidationResult;
import com.auth.jwt.auth.application.exception.TokenExpiredException;
import com.auth.jwt.auth.application.port.TokenValidationPort;
import com.auth.jwt.common.model.CustomPrincipal;
import com.auth.jwt.common.percade.UserQueryFacade;
import com.auth.jwt.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {
  private final UserQueryFacade userQueryFacade;
  private final TokenValidationPort tokenValidationPort;

  public TokenValidationResult validateAccessToken(String token) {
    try {
      if (!tokenValidationPort.validateToken(token)) {
        log.info("유효하지 않은 토큰임");
        return TokenValidationResult.fail("INVALID_TOKEN", "유효하지 않은 토큰입니다.");
      }
    } catch (TokenExpiredException e) {
      log.info("만료된 토큰임");
      return TokenValidationResult.fail(
          e.getExceptionDetail().getCode(), e.getExceptionDetail().getMessage());
    }

    Long userId = extractUserId(token);
    if (userId == null) {
      log.info("토큰에서 사용자 정보를 찾을 수 없음.");
      return TokenValidationResult.fail("INVALID_TOKEN", "유효하지 않은 토큰입니다.");
    }

    if (!userQueryFacade.existsById(userId)) {
      log.info("사용자를 찾을 수 없음");
      return TokenValidationResult.fail("INVALID_TOKEN", "유효하지 않은 토큰입니다.");
    }

    return TokenValidationResult.success(userId);
  }

  public CustomPrincipal getPrincipal(Long userId) {
    User user = userQueryFacade.getById(userId);

    return new CustomPrincipal(user.getId().getValue(), user.getRole().getAuthority());
  }

  private Long extractUserId(String token) {
    try {
      String userIdStr = tokenValidationPort.getUserIdFromToken(token);
      return Long.valueOf(userIdStr);
    } catch (NumberFormatException | NullPointerException e) {
      return null;
    }
  }
}
