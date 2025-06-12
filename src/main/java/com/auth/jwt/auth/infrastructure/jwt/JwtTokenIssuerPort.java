package com.auth.jwt.auth.infrastructure.jwt;

import com.auth.jwt.auth.application.exception.TokenExpiredException;
import com.auth.jwt.auth.application.port.TokenIssuerPort;
import com.auth.jwt.auth.application.port.TokenValidationPort;
import com.auth.jwt.auth.infrastructure.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenIssuerPort implements TokenIssuerPort, TokenValidationPort {
  private final SecretKey key;
  private final long accessTokenExpiration;
  private final JwtProperties jwtProperties;

  public JwtTokenIssuerPort(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenExpiration = jwtProperties.getAccessTokenExpiration();
  }

  @Override
  public String generateAccessToken(Long userId, String username, String authority) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

    return Jwts.builder()
        .subject(userId.toString())
        .claim("auth", authority)
        .claim("username", username)
        .claim("type", "access")
        .issuedAt(now)
        .expiration(expiry)
        .signWith(key)
        .compact();
  }

  @Override
  public String generateRefreshToken(Long userId, String username) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

    return Jwts.builder()
        .subject(userId.toString())
        .claim("type", "refresh")
        .claim("username", username)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(key)
        .compact();
  }

  @Override
  public boolean validateToken(String token) {
    try {
      if (token == null || token.trim().isEmpty()) {
        return false;
      }

      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (ExpiredJwtException expiredException) {
      log.warn("만료된 토큰입니다: {}", expiredException.getMessage());
      throw new TokenExpiredException();
    } catch (MalformedJwtException
        | SignatureException
        | UnsupportedJwtException
        | IllegalArgumentException ex) {
      log.error("JWT token 유효성 검증 실패: {}", ex.getMessage());
      return false;
    }
  }

  @Override
  public boolean validateRefreshToken(String token) {
    return validateToken(token) && isRefreshToken(token);
  }

  @Override
  public String getUserIdFromToken(String token) {
    try {
      Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

      return claims.getSubject();
    } catch (Exception e) {
      log.error("Error extracting user ID from token: {}", e.getMessage());
      return null;
    }
  }

  private boolean isRefreshToken(String token) {
    try {
      Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

      return "refresh".equals(claims.get("type", String.class));
    } catch (Exception e) {
      return false;
    }
  }
}
