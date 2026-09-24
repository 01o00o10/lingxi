// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.middleware.redis;

import java.time.Duration;

public interface LingxiIdempotentStore {
  boolean acquire(String key, Duration ttl);
}
