// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.spi;

import java.util.regex.Pattern;

public class DefaultLingxiTradeCodeValidator implements LingxiTradeCodeValidator {
  private static final Pattern PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{2,31}$");

  public boolean isValid(String tradeCode) {
    return tradeCode != null && PATTERN.matcher(tradeCode).matches();
  }
}
