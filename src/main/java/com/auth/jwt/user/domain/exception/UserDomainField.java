package com.auth.jwt.user.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserDomainField {
  ID("식별자", "가", "는"),
  USERNAME("아이디", "가", "는"),
  PASSWORD("비밀번호", "가", "는"),
  NICKNAME("닉네임", "이", "은"),
  ;
  private final String koreanName;
  private final String koreanSubjectMarker;
  private final String koreanTopicMarker;
}
