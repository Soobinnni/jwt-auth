package com.auth.jwt.auth.application;

import com.auth.jwt.auth.application.dto.command.CredentialCommand;
import com.auth.jwt.auth.application.dto.command.SaveRefreshCommand;
import com.auth.jwt.auth.application.dto.command.TokenReissueCommand;
import com.auth.jwt.auth.application.dto.result.AuthenticationResult;
import com.auth.jwt.auth.application.dto.result.CreateTokenPairResult;
import com.auth.jwt.auth.application.dto.result.TokenReissueResult;
import com.auth.jwt.auth.application.exception.AuthenticationException;
import com.auth.jwt.auth.application.port.DateTimePort;
import com.auth.jwt.auth.application.port.TokenIssuerPort;
import com.auth.jwt.auth.application.port.TokenValidationPort;
import com.auth.jwt.auth.domain.entity.RefreshToken;
import com.auth.jwt.auth.domain.repository.RefreshTokenRepository;
import com.auth.jwt.auth.domain.vo.TokenExpiry;
import com.auth.jwt.auth.domain.vo.TokenValue;
import com.auth.jwt.common.exception.BusinessException;
import com.auth.jwt.common.exception.ExceptionDetail;
import com.auth.jwt.common.exception.NotFoundException;
import com.auth.jwt.common.percade.UserQueryFacade;
import com.auth.jwt.user.domain.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserQueryFacade userQueryFacade;
  private final DateTimePort dateTimePort;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenIssuerPort tokenIssuerPort;
  private final TokenValidationPort tokenValidationPort;

  public AuthenticationResult getUserByCredentialInfo(CredentialCommand info) {
    try {
      userQueryFacade.validateCredentials(info.username(), info.password());

      User user = userQueryFacade.getByUsername(info.username());

      return new AuthenticationResult(user.getId().getValue(), user.getRole().getAuthority());

    } catch (BusinessException | NotFoundException e) {
      ExceptionDetail detail = e.getExceptionDetail();
      log.info(
          "사용자 로그인 정보 인증 절차 중 오류 발생: {}, code: {}, message: {}",
          e,
          detail.getCode(),
          detail.getMessage());
      throw new AuthenticationException(detail.getCode(), detail.getMessage());
    }
  }

  public CreateTokenPairResult createAuthenticationToken(Long userId) {
    try {
      User user = userQueryFacade.getById(userId);
      Long id = user.getId().getValue();
      String username = user.getUsername().getValue();
      String authority = user.getRole().getAuthority();

      String accessToken = tokenIssuerPort.generateAccessToken(id, username, authority);
      String refreshToken = tokenIssuerPort.generateRefreshToken(id, username);

      return new CreateTokenPairResult(accessToken, refreshToken);
    } catch (NotFoundException e) {
      ExceptionDetail detail = e.getExceptionDetail();
      log.info(
          "사용자 조회 중 오류, e: {}, code: {}, message: {}", e, detail.getCode(), detail.getMessage());
      throw new AuthenticationException(detail.getCode(), detail.getMessage());
    }
  }

  public void saveRefreshToken(SaveRefreshCommand command) {
    refreshTokenRepository.deleteByUserId(command.userId());

    TokenExpiry expiry = TokenExpiry.of(dateTimePort.getCurrentDateTime().plusDays(30));
    RefreshToken refreshToken =
        RefreshToken.create(TokenValue.of(command.refreshToken()), command.userId(), expiry);

    refreshTokenRepository.save(refreshToken);
  }

  public TokenReissueResult reIssueTokenPair(TokenReissueCommand command) {
    String refreshToken = command.refreshToken();

    if (!tokenValidationPort.validateRefreshToken(refreshToken)) {
      return TokenReissueResult.fail("INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다.");
    }

    Long userId = extractUserId(refreshToken);
    if (userId == null) {
      return TokenReissueResult.fail("INVALID_TOKEN_PAYLOAD", "토큰에서 사용자 정보를 찾을 수 없습니다.");
    }

    if (!isStoredTokenValid(refreshToken)) {
      return TokenReissueResult.fail("INVALID_REFRESH_TOKEN", "등록되지 않은 리프레시 토큰입니다.");
    }

    if (!userQueryFacade.existsById(userId)) {
      return TokenReissueResult.fail("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
    }

    CreateTokenPairResult newTokenPair = createAuthenticationToken(userId);
    saveRefreshToken(new SaveRefreshCommand(userId, newTokenPair.refreshToken()));

    return TokenReissueResult.success(userId, newTokenPair);
  }

  private Long extractUserId(String token) {
    try {
      String userIdStr = tokenValidationPort.getUserIdFromToken(token);
      return Long.valueOf(userIdStr);
    } catch (NumberFormatException | NullPointerException e) {
      return null;
    }
  }

  private boolean isStoredTokenValid(String refreshToken) {
    TokenValue tokenValue = TokenValue.of(refreshToken);
    Optional<RefreshToken> storedToken = refreshTokenRepository.findByTokenValue(tokenValue);

    if (storedToken.isEmpty()) {
      return false;
    }

    if (storedToken.get().getExpiry().isExpired()) {
      refreshTokenRepository.deleteByTokenValue(tokenValue);
      return false;
    }

    return true;
  }
}
