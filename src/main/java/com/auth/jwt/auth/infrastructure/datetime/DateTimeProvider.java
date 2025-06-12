package com.auth.jwt.auth.infrastructure.datetime;

import com.auth.jwt.auth.application.port.DateTimePort;
import com.auth.jwt.common.utils.SystemDateTimeProvider;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DateTimeProvider implements DateTimePort {
  private final SystemDateTimeProvider systemDateTimeProvider;

  @Override
  public LocalDateTime getCurrentDateTime() {
    return systemDateTimeProvider.getCurrentDateTime();
  }
}
