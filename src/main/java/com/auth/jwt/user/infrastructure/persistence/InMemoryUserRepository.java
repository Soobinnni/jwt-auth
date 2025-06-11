package com.auth.jwt.user.infrastructure.persistence;

import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.repository.UserRepository;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {
  private final Map<UserId, User> userStore = new ConcurrentHashMap<>();
  private final Map<Username, User> usernameIndex = new ConcurrentHashMap<>();

  @Override
  public User save(User user) {
    userStore.put(user.getId(), user);
    usernameIndex.put(user.getUsername(), user);
    return user;
  }

  @Override
  public boolean existsByUsername(Username username) {
    return usernameIndex.containsKey(username);
  }
}
