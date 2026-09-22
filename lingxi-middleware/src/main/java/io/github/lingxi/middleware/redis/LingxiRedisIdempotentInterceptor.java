// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.middleware.redis;

import io.github.lingxi.core.context.LingxiTradeContext;
import io.github.lingxi.core.error.LingxiStandardErrorCode;
import io.github.lingxi.core.exception.LingxiException;
import io.github.lingxi.core.interceptor.LingxiHandlerInterceptor;
import java.time.Duration;

/** Rejects duplicate trade requests using an atomic, expiring key acquired from the store. */
public class LingxiRedisIdempotentInterceptor implements LingxiHandlerInterceptor {
  private final LingxiIdempotentStore store;
  private final Duration ttl;
  private final String keyPrefix;

  public LingxiRedisIdempotentInterceptor(
      LingxiIdempotentStore store, Duration ttl, String keyPrefix) {
    this.store = store;
    this.ttl = ttl;
    this.keyPrefix = keyPrefix;
  }

  public boolean preHandle(LingxiTradeContext context, Object handler) {
    String number = context.getIdempotentNo();
    if (number == null || number.trim().isEmpty())
      throw new LingxiException(
          LingxiStandardErrorCode.VALIDATION_FAILED, "X-Idempotent-No is required");
    // Include the trade code so clients may safely reuse an idempotency number for another API.
    if (!store.acquire(keyPrefix + context.getTradeCode() + ':' + number, ttl))
      throw new LingxiException(LingxiStandardErrorCode.DUPLICATE_REQUEST);
    return true;
  }

  public int order() {
    return -500;
  }
}
