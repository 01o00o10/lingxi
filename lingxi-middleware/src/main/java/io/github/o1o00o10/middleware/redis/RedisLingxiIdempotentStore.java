// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.middleware.redis;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisLingxiIdempotentStore implements LingxiIdempotentStore {
  private final StringRedisTemplate redisTemplate;

  public RedisLingxiIdempotentStore(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public boolean acquire(String key, Duration ttl) {
    return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "1", ttl));
  }
}
