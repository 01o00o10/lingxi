// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.autoconfigure;

import io.github.o1o00o10.middleware.redis.LingxiIdempotentStore;
import io.github.o1o00o10.middleware.redis.LingxiRedisIdempotentInterceptor;
import io.github.o1o00o10.middleware.redis.RedisLingxiIdempotentStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = "lingxi.idempotent", name = "enabled", havingValue = "true")
/** Activates Redis idempotency only when both the Redis API and explicit configuration exist. */
public class LingxiMiddlewareAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public LingxiIdempotentStore lingxiIdempotentStore(StringRedisTemplate template) {
    return new RedisLingxiIdempotentStore(template);
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiRedisIdempotentInterceptor lingxiRedisIdempotentInterceptor(
      LingxiIdempotentStore store, LingxiProperties properties) {
    return new LingxiRedisIdempotentInterceptor(
        store, properties.getIdempotent().getTtl(), properties.getIdempotent().getKeyPrefix());
  }
}
