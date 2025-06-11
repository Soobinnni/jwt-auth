package com.auth.jwt.presentation.controller.user;

import com.auth.jwt.application.service.user.UserService;
import com.auth.jwt.domain.user.User;
import com.auth.jwt.presentation.dto.request.SignupRequest;
import com.auth.jwt.presentation.dto.response.SignupResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @PostMapping
  public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
    User user = userService.signup(request.toCommand());
    SignupResponse response = SignupResponse.from(user);

    return ResponseEntity.created(buildResourceLocation(response.userId())).body(response);
  }

  private URI buildResourceLocation(Long userId) {
    return URI.create("/users/" + userId);
  }
}
