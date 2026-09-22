// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.interceptor;

import io.github.lingxi.core.context.LingxiTradeContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Executes matching interceptors in ascending order and unwinds entered interceptors in reverse
 * order, mirroring nested resource acquisition and release.
 */
public class LingxiInterceptorChain {
  private final List<LingxiHandlerInterceptor> interceptors;
  private final List<LingxiHandlerInterceptor> entered = new ArrayList<LingxiHandlerInterceptor>();
  private boolean completed;

  public LingxiInterceptorChain(List<LingxiHandlerInterceptor> interceptors) {
    this.interceptors = new ArrayList<LingxiHandlerInterceptor>(interceptors);
    Collections.sort(this.interceptors, Comparator.comparingInt(LingxiHandlerInterceptor::order));
  }

  public boolean preHandle(LingxiTradeContext context, Object handler) {
    for (LingxiHandlerInterceptor interceptor : interceptors) {
      if (interceptor.match(context)) {
        // Record before invocation so an interceptor that rejects the request still receives its
        // completion callback.
        entered.add(interceptor);
        if (!interceptor.preHandle(context, handler)) return false;
      }
    }
    return true;
  }

  public void postHandle(LingxiTradeContext context, Object handler, Object result) {
    for (int i = entered.size() - 1; i >= 0; i--)
      entered.get(i).postHandle(context, handler, result);
  }

  public synchronized void complete(
      LingxiTradeContext context, Object handler, Exception exception) {
    // MVC error handling and servlet completion can both reach this method for one request.
    if (completed) return;
    completed = true;
    for (int i = entered.size() - 1; i >= 0; i--) {
      LingxiHandlerInterceptor interceptor = entered.get(i);
      try {
        if (exception != null) interceptor.onException(context, handler, exception);
      } finally {
        interceptor.afterCompletion(context, handler, exception);
      }
    }
  }
}
