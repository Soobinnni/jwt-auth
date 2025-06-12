package com.auth.jwt.auth.application.port;

public interface TokenIssuerPort {
  String generateAccessToken(Long userId, String username, String authority);

  String generateRefreshToken(Long userId, String username);

  boolean validateToken(String token);

  String getUserIdFromToken(String token);
}
