package com.auth.jwt.common.percade;

import com.auth.jwt.common.exception.BusinessException;
import com.auth.jwt.user.domain.entity.User;

public interface UserQueryFacade {
  User getByUsername(String username);

  void validateCredentials(String username, String rawPassword) throws BusinessException;

  User getById(Long id);

  boolean existsById(Long id);
}
