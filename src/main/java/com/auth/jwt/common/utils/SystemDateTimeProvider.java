package com.auth.jwt.common.utils;

import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemDateTimeProvider {
  private final Clock clock;

  public LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now(clock);
  }
}
