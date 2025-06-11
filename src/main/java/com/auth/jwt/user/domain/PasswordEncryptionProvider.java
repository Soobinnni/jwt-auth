package com.auth.jwt.user.domain;

public interface PasswordEncryptionProvider {
  String encode(String plainPassword);

  boolean matches(String plainPassword, String value);
}
