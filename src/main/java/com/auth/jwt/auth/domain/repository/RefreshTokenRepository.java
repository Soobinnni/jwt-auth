package com.auth.jwt.auth.domain.repository;

import com.auth.jwt.auth.domain.entity.RefreshToken;
import com.auth.jwt.auth.domain.vo.TokenValue;
import java.util.Optional;

public interface RefreshTokenRepository {
  RefreshToken save(RefreshToken refreshToken);

  Optional<RefreshToken> findByTokenValue(TokenValue tokenValue);

  void deleteByUserId(Long userId);

  void deleteByTokenValue(TokenValue tokenValue);
}
