// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.spi;

import io.github.o1o00o10.core.context.LingxiTradeContext;

public interface LingxiResponseWrapper {
  Object wrap(Object body, LingxiTradeContext context);
}
