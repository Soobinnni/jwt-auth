package com.auth.jwt.user.application;

import com.auth.jwt.user.application.dto.command.RoleGrantCommand;
import com.auth.jwt.user.application.dto.command.SignupCommand;
import com.auth.jwt.user.application.exception.UserAlreadyExistsException;
import com.auth.jwt.user.application.exception.UserExceptionHandler;
import com.auth.jwt.user.application.exception.UserNotFoundException;
import com.auth.jwt.user.domain.entity.Role;
import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.repository.UserRepository;
import com.auth.jwt.user.domain.service.IdGenerator;
import com.auth.jwt.user.domain.service.PasswordEncryptionProvider;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@UserExceptionHandler
@RequiredArgsConstructor
public class UserCommandService {
  private final UserRepository userRepository;
  private final IdGenerator idGenerator;
  private final PasswordEncryptionProvider encryptionProvider;

  public User signup(SignupCommand command) {

    String usernameValue = command.username();

    if (userRepository.existsByUsername(Username.of(usernameValue))) {
      throw new UserAlreadyExistsException();
    }

    User user =
        User.create(
            idGenerator, usernameValue, command.password(), encryptionProvider, command.nickname());
    User savedUser = userRepository.save(user);
    log.info("신규 회원 가입, username: {}", savedUser.getUsername());

    return savedUser;
  }

  public User grantAdminRole(RoleGrantCommand command) {
    User user =
        userRepository
            .findById(UserId.of(command.userId()))
            .orElseThrow(() -> new UserNotFoundException());
    User grantAdminRoleUser = user.grantAdminRole();
    User updatedUser = userRepository.update(grantAdminRoleUser);
    log.info("Admin role 이 다음 유저에게 부여됨: {}", updatedUser.toString());

    return updatedUser;
  }

  public User createUser(long id, SignupCommand command, Role role) {
    User user =
        User.create(
            id,
            command.username(),
            command.password(),
            encryptionProvider,
            command.nickname(),
            role);
    User savedUser = userRepository.save(user);
    return savedUser;
  }
}
