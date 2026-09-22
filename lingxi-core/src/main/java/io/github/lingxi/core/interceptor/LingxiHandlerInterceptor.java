// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.interceptor;

import io.github.lingxi.core.context.LingxiTradeContext;

public interface LingxiHandlerInterceptor {
  default boolean preHandle(LingxiTradeContext context, Object handler) {
    return true;
  }

  default void postHandle(LingxiTradeContext context, Object handler, Object result) {}

  default void onException(LingxiTradeContext context, Object handler, Exception exception) {}

  default void afterCompletion(LingxiTradeContext context, Object handler, Exception exception) {}

  default boolean match(LingxiTradeContext context) {
    return true;
  }

  default int order() {
    return 0;
  }
}
