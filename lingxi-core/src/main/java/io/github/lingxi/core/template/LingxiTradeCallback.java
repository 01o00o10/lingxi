// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.template;

import io.github.lingxi.core.context.LingxiTradeContext;

public interface LingxiTradeCallback<T> {
  T execute(LingxiTradeContext context) throws Exception;
}
