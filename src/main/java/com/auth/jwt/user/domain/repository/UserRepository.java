package com.auth.jwt.user.domain.repository;

import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import java.util.Optional;

public interface UserRepository {
  boolean existsByUsername(Username username);

  boolean existsById(UserId userId); // DDD 개선: 존재 여부만 확인하는 메서드 추가

  User save(User user);

  Optional<User> findById(UserId userId);

  Optional<User> findByUsername(Username username);

  User update(User grantAdminRoleUser);
}
