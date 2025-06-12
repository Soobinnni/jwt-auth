package com.auth.jwt.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  /** JWT 서명에 사용할 비밀키 (Base64 인코딩된 256비트 키) */
  private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

  /** Access Token 만료 시간 (밀리초) 기본값: 1시간 (3600000ms) */
  private long accessTokenExpiration = 3600000L;

  /** Refresh Token 만료 시간 (밀리초) 기본값: 30일 (2592000000ms) */
  private long refreshTokenExpiration = 2592000000L;

  /** JWT 토큰 발급자 */
  private String issuer = "jwt-auth-service";

  /** JWT 토큰 대상 */
  private String audience = "jwt-auth-client";
}
