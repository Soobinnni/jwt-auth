package com.auth.jwt.auth.application.port;

import com.auth.jwt.auth.application.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenStoragePort {
  RefreshToken save(RefreshToken refreshToken);

  Optional<RefreshToken> findByTokenValue(String tokenValue);

  void deleteByUserId(Long userId);

  void deleteByTokenValue(String tokenValue);
}
