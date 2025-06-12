package com.auth.jwt.auth.application.port;

import java.time.LocalDateTime;

public interface DateTimePort {
  LocalDateTime getCurrentDateTime();
}
