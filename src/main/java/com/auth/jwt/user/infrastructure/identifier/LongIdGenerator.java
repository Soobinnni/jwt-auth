package com.auth.jwt.user.infrastructure.identifier;

import com.auth.jwt.user.domain.service.IdGenerator;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class LongIdGenerator implements IdGenerator {
  private final AtomicLong sequence = new AtomicLong(System.currentTimeMillis());

  @Override
  public Long generate() {
    return sequence.incrementAndGet();
  }
}
