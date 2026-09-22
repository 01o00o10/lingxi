// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.security.jwt;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LingxiJwtPrincipal implements Principal {
  private final String name;
  private final List<String> authorities;
  private final Map<String, Object> claims;

  public LingxiJwtPrincipal(String name, List<String> authorities, Map<String, Object> claims) {
    this.name = name;
    this.authorities = Collections.unmodifiableList(authorities);
    this.claims = Collections.unmodifiableMap(claims);
  }

  public String getName() {
    return name;
  }

  public List<String> getAuthorities() {
    return authorities;
  }

  public Map<String, Object> getClaims() {
    return claims;
  }
}
