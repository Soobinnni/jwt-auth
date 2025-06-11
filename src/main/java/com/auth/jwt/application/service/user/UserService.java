package com.auth.jwt.application.service.user;

import com.auth.jwt.application.dto.auth.SignupCommand;
import com.auth.jwt.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  public User signup(SignupCommand command) {
    return null;
  }
}
