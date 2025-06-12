package com.auth.jwt.auth.infrastructure.persistence;

import com.auth.jwt.auth.application.model.RefreshToken;
import com.auth.jwt.auth.application.port.RefreshTokenStoragePort;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRefreshTokenStorageAdapter implements RefreshTokenStoragePort {
  private final Map<String, RefreshToken> tokenStore = new ConcurrentHashMap<>();
  private final Map<Long, RefreshToken> userIndex = new ConcurrentHashMap<>();

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    tokenStore.put(refreshToken.tokenValue(), refreshToken);
    userIndex.put(refreshToken.userId(), refreshToken);
    return refreshToken;
  }

  @Override
  public Optional<RefreshToken> findByTokenValue(String tokenValue) {
    return Optional.ofNullable(tokenStore.get(tokenValue));
  }

  @Override
  public void deleteByUserId(Long userId) {
    RefreshToken token = userIndex.remove(userId);
    if (token != null) {
      tokenStore.remove(token.tokenValue());
    }
  }

  @Override
  public void deleteByTokenValue(String tokenValue) {
    RefreshToken token = tokenStore.remove(tokenValue);
    if (token != null) {
      userIndex.remove(token.userId());
    }
  }
}
