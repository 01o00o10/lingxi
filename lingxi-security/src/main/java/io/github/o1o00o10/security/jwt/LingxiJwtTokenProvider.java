// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LingxiJwtTokenProvider {
  private final Key key;
  private final Duration validity;

  public LingxiJwtTokenProvider(String secret, Duration validity) {
    byte[] bytes =
        secret != null && secret.startsWith("base64:")
            ? Decoders.BASE64.decode(secret.substring("base64:".length()))
            : secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
    if (bytes.length < 32)
      throw new IllegalArgumentException("lingxi.auth.jwt.secret must contain at least 256 bits");
    this.key = Keys.hmacShaKeyFor(bytes);
    this.validity = validity;
  }

  public String createToken(
      String subject, Collection<String> authorities, Map<String, Object> claims) {
    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .claim("authorities", authorities)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + validity.toMillis()))
        .signWith(key)
        .compact();
  }

  public LingxiJwtPrincipal parse(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    Object raw = claims.get("authorities");
    List<String> authorities = new ArrayList<String>();
    if (raw instanceof Collection)
      for (Object item : (Collection<?>) raw) authorities.add(String.valueOf(item));
    return new LingxiJwtPrincipal(claims.getSubject(), authorities, claims);
  }
}
