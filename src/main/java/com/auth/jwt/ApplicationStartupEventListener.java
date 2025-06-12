package com.auth.jwt;

import com.auth.jwt.user.application.UserCommandService;
import com.auth.jwt.user.application.dto.command.SignupCommand;
import com.auth.jwt.user.application.exception.UserAlreadyExistsException;
import com.auth.jwt.user.domain.entity.Role;
import com.auth.jwt.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupEventListener {
  private final UserCommandService userCommandService;

  @EventListener(ApplicationReadyEvent.class)
  public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
    log.info("애플리케이션 시작 완료 - 기본 관리자 및 일반 계정 설정 시작");
    createDefaultUsers();
  }

  private void createDefaultUsers() {
    try {
      User adminUser =
          userCommandService.createUser(
              1L, new SignupCommand("admin", "admin123", "관리자"), Role.ADMIN);
      User user =
          userCommandService.createUser(
              2L, new SignupCommand("user", "user1234", "사용자"), Role.USER);
      log.info("기본 계정이 생성되었습니다.");
      log.info("관리자 정보 : {}", adminUser.toString());
      log.info("일반 사용자 정보 : {}", user.toString());
    } catch (UserAlreadyExistsException e) {
      log.info("계정이 이미 존재합니다.");
    } catch (Exception e) {
      log.error("기본 계정 생성 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
