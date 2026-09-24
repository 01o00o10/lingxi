// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.interceptor;

import io.github.lingxi.core.annotation.LingxiInterceptor;
import io.github.lingxi.core.context.LingxiTradeContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * Executes matching interceptors in ascending order and unwinds entered interceptors in reverse
 * order, mirroring nested resource acquisition and release.
 */
public class LingxiInterceptorChain {
  private final List<Binding> interceptors;
  private final List<Binding> entered = new ArrayList<Binding>();
  private boolean serviceCompleted;
  private boolean completed;

  public LingxiInterceptorChain(List<LingxiHandlerInterceptor> interceptors) {
    this.interceptors = new ArrayList<Binding>();
    for (LingxiHandlerInterceptor interceptor : interceptors)
      this.interceptors.add(new Binding(interceptor, stage(interceptor)));
    Collections.sort(
        this.interceptors, Comparator.comparingInt(binding -> binding.interceptor.order()));
  }

  public boolean preHandle(LingxiTradeContext context, Object handler) {
    for (Binding binding : interceptors) {
      LingxiHandlerInterceptor interceptor = binding.interceptor;
      if (interceptor.match(context)) {
        // Record before invocation so an interceptor that rejects the request still receives its
        // completion callback.
        entered.add(binding);
        if (runs(binding.stage, LingxiInterceptorStage.BEFORE_SERVICE)
            && !interceptor.preHandle(context, handler)) return false;
      }
    }
    return true;
  }

  public synchronized void postHandle(LingxiTradeContext context, Object handler, Object result) {
    if (serviceCompleted) return;
    serviceCompleted = true;
    for (int i = entered.size() - 1; i >= 0; i--)
      if (runs(entered.get(i).stage, LingxiInterceptorStage.AFTER_SERVICE))
        entered.get(i).interceptor.postHandle(context, handler, result);
  }

  public synchronized void complete(
      LingxiTradeContext context, Object handler, Exception exception) {
    // MVC error handling and servlet completion can both reach this method for one request.
    if (completed) return;
    completed = true;
    if (exception != null) {
      for (int i = entered.size() - 1; i >= 0; i--) {
        Binding binding = entered.get(i);
        if (runs(binding.stage, LingxiInterceptorStage.ON_EXCEPTION))
          binding.interceptor.onException(context, handler, exception);
      }
    }
    for (int i = entered.size() - 1; i >= 0; i--) {
      Binding binding = entered.get(i);
      if (runs(binding.stage, LingxiInterceptorStage.AFTER_COMPLETION))
        binding.interceptor.afterCompletion(context, handler, exception);
    }
  }

  private LingxiInterceptorStage stage(LingxiHandlerInterceptor interceptor) {
    LingxiInterceptor annotation =
        AnnotatedElementUtils.findMergedAnnotation(interceptor.getClass(), LingxiInterceptor.class);
    if (annotation != null && annotation.stage() != LingxiInterceptorStage.ALL)
      return annotation.stage();
    return interceptor.stage();
  }

  private boolean runs(LingxiInterceptorStage configured, LingxiInterceptorStage current) {
    LingxiInterceptorStage normalized = configured.normalized();
    return normalized == LingxiInterceptorStage.ALL || normalized == current;
  }

  private static final class Binding {
    private final LingxiHandlerInterceptor interceptor;
    private final LingxiInterceptorStage stage;

    private Binding(LingxiHandlerInterceptor interceptor, LingxiInterceptorStage stage) {
      this.interceptor = interceptor;
      this.stage = stage;
    }
  }
}
