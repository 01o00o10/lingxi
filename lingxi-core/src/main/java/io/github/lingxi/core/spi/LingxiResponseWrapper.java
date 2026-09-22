// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.spi;

import io.github.lingxi.core.context.LingxiTradeContext;

public interface LingxiResponseWrapper {
  Object wrap(Object body, LingxiTradeContext context);
}
