package com.auth.jwt.auth.infrastructure.persistence;

import com.auth.jwt.auth.domain.entity.RefreshToken;
import com.auth.jwt.auth.domain.repository.RefreshTokenRepository;
import com.auth.jwt.auth.domain.vo.TokenValue;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {
  private final Map<TokenValue, RefreshToken> tokenStore = new ConcurrentHashMap<>();
  private final Map<Long, RefreshToken> userIndex = new ConcurrentHashMap<>();

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    tokenStore.put(refreshToken.getTokenValue(), refreshToken);
    userIndex.put(refreshToken.getUserId(), refreshToken);
    return refreshToken;
  }

  @Override
  public Optional<RefreshToken> findByTokenValue(TokenValue tokenValue) {
    return Optional.ofNullable(tokenStore.get(tokenValue));
  }

  @Override
  public void deleteByUserId(Long userId) {
    RefreshToken token = userIndex.remove(userId);
    if (token != null) {
      tokenStore.remove(token.getTokenValue());
    }
  }

  @Override
  public void deleteByTokenValue(TokenValue tokenValue) {
    RefreshToken token = tokenStore.remove(tokenValue);
    if (token != null) {
      userIndex.remove(token.getUserId());
    }
  }
}
