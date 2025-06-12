package com.auth.jwt.auth.application.port;

public interface TokenValidationPort {
  boolean validateToken(String token);

  String getUserIdFromToken(String token);

  boolean validateRefreshToken(String refreshToken);
}
