package com.auth.jwt.auth.presentation.dto.request;

import com.auth.jwt.auth.application.dto.command.TokenReissueCommand;

public record TokenReissueRequest(String accessToken, String refreshToken) {
  public TokenReissueCommand toCommand() {
    return new TokenReissueCommand(accessToken, refreshToken);
  }
}
