package com.auth.jwt.user.infrastructure.persistence;

import com.auth.jwt.user.domain.entity.User;
import com.auth.jwt.user.domain.repository.UserRepository;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import java.util.Map;
import java.util.Optional;
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

  @Override
  public boolean existsById(UserId userId) {
    return userStore.containsKey(userId);
  }

  @Override
  public Optional<User> findById(UserId userId) {
    return Optional.ofNullable(userStore.get(userId));
  }

  @Override
  public Optional<User> findByUsername(Username username) {
    return Optional.ofNullable(usernameIndex.get(username));
  }

  @Override
  public User update(User updatedUser) {
    deleteById(updatedUser.getId());
    return save(updatedUser);
  }

  private void deleteById(UserId userId) {
    User user = userStore.remove(userId);
    if (user != null) {
      usernameIndex.remove(user.getUsername());
    }
  }
}
