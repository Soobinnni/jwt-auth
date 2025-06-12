package com.auth.jwt.user.domain.entity;

import com.auth.jwt.user.domain.service.IdGenerator;
import com.auth.jwt.user.domain.service.PasswordEncryptionProvider;
import com.auth.jwt.user.domain.vo.Nickname;
import com.auth.jwt.user.domain.vo.Password;
import com.auth.jwt.user.domain.vo.UserId;
import com.auth.jwt.user.domain.vo.Username;
import lombok.Getter;

@Getter
public class User {
  private final UserId id;
  private final Username username;
  private final Password password;
  private final Nickname nickname;
  private final Role role;

  private User(UserId id, Username username, Password password, Nickname nickname, Role role) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.nickname = nickname;
    this.role = role;
  }

  public static User create(
      IdGenerator idGenerator,
      String username,
      String password,
      PasswordEncryptionProvider encryptionProvider,
      String nickname) {
    Long userId = idGenerator.generate();
    return User.create(userId, username, password, encryptionProvider, nickname, Role.getDefault());
  }

  public static User create(
      Long id,
      String username,
      String password,
      PasswordEncryptionProvider encryptionProvider,
      String nickname,
      Role role) {
    return new User(
        UserId.of(id),
        Username.of(username),
        Password.of(password, encryptionProvider),
        Nickname.of(nickname),
        role);
  }

  public User grantAdminRole() {
    return new User(id, username, password, nickname, Role.ADMIN);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User user = (User) obj;
    return id.equals(user.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "User: id= "
        + id.toString()
        + ", username= "
        + username.toString()
        + ", nickname= "
        + nickname.toString()
        + ", role= "
        + role.getAuthority();
  }
}
