// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.security.auth;

import io.github.o1o00o10.core.annotation.LingxiApi;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

public interface LingxiLoginApi {
  @LingxiApi(
      path = "/auth/login",
      method = RequestMethod.POST,
      tradeCode = "AUTH_LOGIN",
      auth = false)
  LoginResponse login(@RequestBody @Valid LoginRequest request);

  class LoginRequest {
    @NotBlank private String username;
    @NotBlank private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String v) {
      username = v;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String v) {
      password = v;
    }
  }

  class LoginResponse {
    private final String token;
    private final String tokenType;
    private final long expiresIn;

    public LoginResponse(String token, String tokenType, long expiresIn) {
      this.token = token;
      this.tokenType = tokenType;
      this.expiresIn = expiresIn;
    }

    public String getToken() {
      return token;
    }

    public String getTokenType() {
      return tokenType;
    }

    public long getExpiresIn() {
      return expiresIn;
    }
  }
}
