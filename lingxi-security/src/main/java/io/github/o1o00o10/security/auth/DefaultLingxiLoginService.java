// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.security.auth;

import io.github.o1o00o10.security.jwt.LingxiJwtTokenProvider;
import java.time.Duration;

public class DefaultLingxiLoginService implements LingxiLoginApi {
  private final LingxiAuthenticationService authenticationService;
  private final LingxiJwtTokenProvider tokenProvider;
  private final Duration expiry;

  public DefaultLingxiLoginService(
      LingxiAuthenticationService authenticationService,
      LingxiJwtTokenProvider tokenProvider,
      Duration expiry) {
    this.authenticationService = authenticationService;
    this.tokenProvider = tokenProvider;
    this.expiry = expiry;
  }

  public LoginResponse login(LoginRequest request) {
    LingxiAuthenticationService.AuthenticatedUser user =
        authenticationService.authenticate(request.getUsername(), request.getPassword());
    return new LoginResponse(
        tokenProvider.createToken(user.getUserId(), user.getAuthorities(), user.getClaims()),
        "Bearer",
        expiry.getSeconds());
  }
}
