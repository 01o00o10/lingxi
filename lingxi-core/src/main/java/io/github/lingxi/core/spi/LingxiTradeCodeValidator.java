// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.spi;

public interface LingxiTradeCodeValidator {
  boolean isValid(String tradeCode);
}
