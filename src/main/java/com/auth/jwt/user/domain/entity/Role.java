package com.auth.jwt.user.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  USER("일반 사용자"),
  ADMIN("관리자"),
  ;

  private final String description;

  public static Role getDefault() {
    return USER;
  }

  public String getAuthority() {
    return "ROLE_" + this.name();
  }
}
