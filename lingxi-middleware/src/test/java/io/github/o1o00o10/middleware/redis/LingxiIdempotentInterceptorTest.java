// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.middleware.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.o1o00o10.core.context.LingxiTradeContext;
import io.github.o1o00o10.core.exception.LingxiException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class LingxiIdempotentInterceptorTest {
  @Test
  void acceptsFirstRequestAndRejectsDuplicate() {
    Set<String> keys = new HashSet<String>();
    LingxiIdempotentStore store = (key, ttl) -> keys.add(key);
    LingxiRedisIdempotentInterceptor interceptor =
        new LingxiRedisIdempotentInterceptor(store, Duration.ofMinutes(5), "test:");
    LingxiTradeContext context = new LingxiTradeContext();
    context.setTradeCode("PAY_CREATE");
    context.setIdempotentNo("idem-1");
    assertThat(interceptor.preHandle(context, this)).isTrue();
    assertThatThrownBy(() -> interceptor.preHandle(context, this))
        .isInstanceOf(LingxiException.class)
        .hasMessage("Duplicate request");
  }

  @Test
  void requiresIdempotencyNumber() {
    LingxiRedisIdempotentInterceptor interceptor =
        new LingxiRedisIdempotentInterceptor((key, ttl) -> true, Duration.ofMinutes(5), "test:");
    assertThatThrownBy(() -> interceptor.preHandle(new LingxiTradeContext(), this))
        .isInstanceOf(LingxiException.class)
        .hasMessage("X-Idempotent-No is required");
  }

  @Test
  void delegatesAtomicAcquisitionToRedisWithTtl() {
    StringRedisTemplate template = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> operations = mock(ValueOperations.class);
    Duration ttl = Duration.ofSeconds(30);
    when(template.opsForValue()).thenReturn(operations);
    when(operations.setIfAbsent("key", "1", ttl)).thenReturn(Boolean.TRUE);

    assertThat(new RedisLingxiIdempotentStore(template).acquire("key", ttl)).isTrue();
    verify(operations).setIfAbsent("key", "1", ttl);
  }
}
