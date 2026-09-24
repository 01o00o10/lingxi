// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.lingxi.core.context.LingxiTradeContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class LingxiInterceptorChainTest {
  @Test
  void executesPreAscendingAndCompletionDescendingOnce() {
    List<String> events = new ArrayList<String>();
    LingxiHandlerInterceptor first = interceptor(10, "second", events);
    LingxiHandlerInterceptor second = interceptor(0, "first", events);
    LingxiInterceptorChain chain = new LingxiInterceptorChain(Arrays.asList(first, second));
    LingxiTradeContext context = new LingxiTradeContext();
    chain.preHandle(context, this);
    chain.postHandle(context, this, "ok");
    chain.complete(context, this, null);
    chain.complete(context, this, null);
    assertThat(events)
        .containsExactly(
            "pre:first", "pre:second", "post:second", "post:first", "done:second", "done:first");
  }

  @Test
  void skipsNonMatchingInterceptorsAndStopsAfterRejection() {
    List<String> events = new ArrayList<String>();
    LingxiHandlerInterceptor skipped =
        new LingxiHandlerInterceptor() {
          public boolean match(LingxiTradeContext context) {
            return false;
          }

          public boolean preHandle(LingxiTradeContext context, Object handler) {
            events.add("skipped");
            return true;
          }
        };
    LingxiHandlerInterceptor rejecting =
        new LingxiHandlerInterceptor() {
          public boolean preHandle(LingxiTradeContext context, Object handler) {
            events.add("reject");
            return false;
          }
        };
    LingxiHandlerInterceptor unreachable = interceptor(20, "unreachable", events);

    LingxiInterceptorChain chain =
        new LingxiInterceptorChain(Arrays.asList(skipped, rejecting, unreachable));

    assertThat(chain.preHandle(new LingxiTradeContext(), this)).isFalse();
    assertThat(events).containsExactly("reject");
  }

  @Test
  void notifiesEnteredInterceptorsAboutExceptionsBeforeCompletion() {
    List<String> events = new ArrayList<String>();
    LingxiHandlerInterceptor interceptor =
        new LingxiHandlerInterceptor() {
          public void onException(LingxiTradeContext context, Object handler, Exception exception) {
            events.add("error:" + exception.getMessage());
          }

          public void afterCompletion(
              LingxiTradeContext context, Object handler, Exception exception) {
            events.add("done");
          }
        };
    LingxiInterceptorChain chain =
        new LingxiInterceptorChain(Collections.singletonList(interceptor));
    chain.preHandle(new LingxiTradeContext(), this);
    chain.complete(new LingxiTradeContext(), this, new IllegalStateException("failed"));
    chain.complete(new LingxiTradeContext(), this, new IllegalStateException("again"));

    assertThat(events).containsExactly("error:failed", "done");
  }

  @Test
  void runsOnlyTheConfiguredLifecycleStage() {
    List<String> events = new ArrayList<String>();
    LingxiHandlerInterceptor pre = staged(LingxiInterceptorStage.BEFORE_SERVICE, "pre", events);
    LingxiHandlerInterceptor post = staged(LingxiInterceptorStage.AFTER_SERVICE, "post", events);
    LingxiHandlerInterceptor error = staged(LingxiInterceptorStage.ON_EXCEPTION, "error", events);
    LingxiHandlerInterceptor completion =
        staged(LingxiInterceptorStage.AFTER_COMPLETION, "complete", events);
    LingxiInterceptorChain chain =
        new LingxiInterceptorChain(Arrays.asList(pre, post, error, completion));
    LingxiTradeContext context = new LingxiTradeContext();

    assertThat(chain.preHandle(context, this)).isTrue();
    chain.postHandle(context, this, "ok");
    chain.complete(context, this, new IllegalStateException("failed"));

    assertThat(events).containsExactly("pre:pre", "post:post", "error:error", "done:complete");
  }

  private LingxiHandlerInterceptor staged(
      final LingxiInterceptorStage stage, final String name, final List<String> events) {
    return new LingxiHandlerInterceptor() {
      public boolean preHandle(LingxiTradeContext c, Object h) {
        events.add("pre:" + name);
        return true;
      }

      public void postHandle(LingxiTradeContext c, Object h, Object r) {
        events.add("post:" + name);
      }

      public void onException(LingxiTradeContext c, Object h, Exception e) {
        events.add("error:" + name);
      }

      public void afterCompletion(LingxiTradeContext c, Object h, Exception e) {
        events.add("done:" + name);
      }

      public LingxiInterceptorStage stage() {
        return stage;
      }
    };
  }

  private LingxiHandlerInterceptor interceptor(
      final int order, final String name, final List<String> events) {
    return new LingxiHandlerInterceptor() {
      public boolean preHandle(LingxiTradeContext c, Object h) {
        events.add("pre:" + name);
        return true;
      }

      public void postHandle(LingxiTradeContext c, Object h, Object r) {
        events.add("post:" + name);
      }

      public void afterCompletion(LingxiTradeContext c, Object h, Exception e) {
        events.add("done:" + name);
      }

      public int order() {
        return order;
      }
    };
  }
}
