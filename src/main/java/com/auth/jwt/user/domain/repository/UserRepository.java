package com.auth.jwt.user.domain.repository;

import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.vo.Username;

public interface UserRepository {
  boolean existsByUsername(Username username);

  User save(User user);
}
