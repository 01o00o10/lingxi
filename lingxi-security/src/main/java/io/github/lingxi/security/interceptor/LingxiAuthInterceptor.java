// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.security.interceptor;

import io.github.lingxi.core.annotation.LingxiApi;
import io.github.lingxi.core.context.LingxiTradeContext;
import io.github.lingxi.core.error.LingxiStandardErrorCode;
import io.github.lingxi.core.exception.LingxiException;
import io.github.lingxi.core.interceptor.LingxiHandlerInterceptor;
import io.github.lingxi.core.util.LingxiAnnotationUtils;
import io.github.lingxi.security.annotation.LingxiAuthorize;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;

/** Enforces the authentication and authorization policy declared on Lingxi contract methods. */
public class LingxiAuthInterceptor implements LingxiHandlerInterceptor {
  @Override
  public boolean preHandle(LingxiTradeContext context, Object handler) {
    if (!(handler instanceof HandlerMethod)) return true;
    HandlerMethod method = (HandlerMethod) handler;
    LingxiApi api = LingxiAnnotationUtils.findApi(method.getMethod(), method.getBeanType());
    if (api == null || !api.auth()) return true;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || "anonymousUser".equals(authentication.getPrincipal()))
      throw new LingxiException(LingxiStandardErrorCode.UNAUTHENTICATED);
    context.setUserId(authentication.getName());
    LingxiAuthorize authorize =
        LingxiAnnotationUtils.findMethodAnnotation(
            method.getMethod(), method.getBeanType(), LingxiAuthorize.class);
    if (authorize != null) verify(authorize, authentication);
    return true;
  }

  private void verify(LingxiAuthorize rule, Authentication authentication) {
    Set<String> granted = new HashSet<String>();
    for (GrantedAuthority authority : authentication.getAuthorities())
      granted.add(authority.getAuthority());
    for (String role : rule.roles())
      if (!granted.contains(role.startsWith("ROLE_") ? role : "ROLE_" + role)) deny();
    for (String authority : rule.authorities()) if (!granted.contains(authority)) deny();
  }

  private void deny() {
    throw new LingxiException(LingxiStandardErrorCode.FORBIDDEN);
  }

  @Override
  public int order() {
    // Authentication must populate userId before middleware such as idempotency and auditing runs.
    return -800;
  }
}
