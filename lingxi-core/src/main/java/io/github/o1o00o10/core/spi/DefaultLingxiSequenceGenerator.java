// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.spi;

import java.util.UUID;

public class DefaultLingxiSequenceGenerator implements LingxiSequenceGenerator {
  public String next(String kind) {
    return kind
        + System.currentTimeMillis()
        + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }
}
