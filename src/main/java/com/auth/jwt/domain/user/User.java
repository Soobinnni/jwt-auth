package com.auth.jwt.domain.user;

import com.auth.jwt.domain.user.vo.Nickname;
import com.auth.jwt.domain.user.vo.Password;
import com.auth.jwt.domain.user.vo.UserId;
import com.auth.jwt.domain.user.vo.Username;
import java.util.Set;
import lombok.Getter;

@Getter
public class User {
  private final UserId id;
  private final Username username;
  private final Password password;
  private final Nickname nickname;
  private final Set<Role> roles;

  public User(UserId id, Username username, Password password, Nickname nickname, Set<Role> roles) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.nickname = nickname;
    this.roles = roles;
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
}
