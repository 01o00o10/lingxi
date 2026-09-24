// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.sample.interceptor;

import io.github.o1o00o10.core.context.LingxiTradeContext;
import io.github.o1o00o10.core.interceptor.LingxiHandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class SampleAuditInterceptor implements LingxiHandlerInterceptor {
  public boolean preHandle(LingxiTradeContext context, Object handler) {
    if (handler instanceof HandlerMethod) {
      HttpServletRequest request = currentRequest();
      if (request != null && request.getHeader("X-Tenant") != null) {
        context.getExt().put("tenant", request.getHeader("X-Tenant"));
      }
    }
    return true;
  }

  private HttpServletRequest currentRequest() {
    org.springframework.web.context.request.RequestAttributes attributes =
        org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
    if (!(attributes instanceof org.springframework.web.context.request.ServletRequestAttributes)) {
      return null;
    }
    return ((org.springframework.web.context.request.ServletRequestAttributes) attributes)
        .getRequest();
  }
}
