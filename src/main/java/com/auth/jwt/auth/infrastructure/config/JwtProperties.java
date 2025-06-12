package com.auth.jwt.auth.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  private String secretKey;
  private long accessTokenExpiration;
  private long refreshTokenExpiration;
  private String issuer;
  private String audience;
}
