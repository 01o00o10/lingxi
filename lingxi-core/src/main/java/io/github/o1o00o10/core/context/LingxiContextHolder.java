// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.context;

public final class LingxiContextHolder {
  public static final String REQUEST_ATTRIBUTE = LingxiContextHolder.class.getName() + ".CONTEXT";
  private static final ThreadLocal<LingxiTradeContext> HOLDER =
      new ThreadLocal<LingxiTradeContext>();

  private LingxiContextHolder() {}

  public static void set(LingxiTradeContext context) {
    HOLDER.set(context);
  }

  public static LingxiTradeContext get() {
    return HOLDER.get();
  }

  public static void clear() {
    HOLDER.remove();
  }
}
