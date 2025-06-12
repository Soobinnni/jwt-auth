package com.auth.jwt.user.application;

import com.auth.jwt.common.percade.UserQueryFacade;
import com.auth.jwt.user.application.exception.UserBusinessException;
import com.auth.jwt.user.application.exception.UserNotFoundException;
import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.repository.UserRepository;
import com.auth.jwt.user.domain.service.PasswordEncryptionProvider;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService implements UserQueryFacade {

  private final UserRepository userRepository;
  private final PasswordEncryptionProvider passwordEncryptionProvider;

  @Override
  public void validateCredentials(String username, String rawPassword) {
    User user =
        userRepository
            .findByUsername(Username.of(username))
            .orElseThrow(
                () -> new UserBusinessException("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다."));

    if (!user.getPassword().matches(rawPassword, passwordEncryptionProvider)) {
      throw new UserBusinessException("NOT_MATCH_USER_INFO", "아이디 또는 비밀번호가 올바르지 않습니다.");
    }
  }

  @Override
  public User getById(Long id) {
    return userRepository.findById(UserId.of(id)).orElseThrow(() -> new UserNotFoundException());
  }

  @Override
  public boolean existsById(Long id) {
    return userRepository.existsById(UserId.of(id));
  }

  @Override
  public User getByUsername(String username) {
    return userRepository
        .findByUsername(Username.of(username))
        .orElseThrow(() -> new UserNotFoundException());
  }
}
