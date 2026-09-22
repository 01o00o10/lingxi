// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.security.jwt;

import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class LingxiJwtAuthenticationFilter extends OncePerRequestFilter {
  private final LingxiJwtTokenProvider provider;
  private final String header;
  private final String prefix;

  public LingxiJwtAuthenticationFilter(
      LingxiJwtTokenProvider provider, String header, String prefix) {
    this.provider = provider;
    this.header = header;
    this.prefix = prefix;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String value = request.getHeader(header);
    if (value != null
        && value.startsWith(prefix)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        LingxiJwtPrincipal principal = provider.parse(value.substring(prefix.length()).trim());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                principal,
                value,
                principal.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (RuntimeException ignored) {
        SecurityContextHolder.clearContext();
      }
    }
    chain.doFilter(request, response);
  }
}
