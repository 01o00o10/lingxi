// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.security.auth;

import java.util.Collection;
import java.util.Map;

public interface LingxiAuthenticationService {
  AuthenticatedUser authenticate(String username, String password);

  class AuthenticatedUser {
    private final String userId;
    private final Collection<String> authorities;
    private final Map<String, Object> claims;

    public AuthenticatedUser(
        String userId, Collection<String> authorities, Map<String, Object> claims) {
      this.userId = userId;
      this.authorities = authorities;
      this.claims = claims;
    }

    public String getUserId() {
      return userId;
    }

    public Collection<String> getAuthorities() {
      return authorities;
    }

    public Map<String, Object> getClaims() {
      return claims;
    }
  }
}
