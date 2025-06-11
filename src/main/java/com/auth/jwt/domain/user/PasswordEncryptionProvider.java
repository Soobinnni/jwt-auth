package com.auth.jwt.domain.user;

public interface PasswordEncryptionProvider {
  String encode(String plainPassword);

  boolean matches(String plainPassword, String value);
}
