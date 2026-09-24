// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.spi;

import io.github.o1o00o10.core.context.LingxiTradeContext;
import io.github.o1o00o10.core.model.LingxiResponse;

public class DefaultLingxiResponseWrapper implements LingxiResponseWrapper {
  public Object wrap(Object body, LingxiTradeContext context) {
    if (body instanceof LingxiResponse) return body;
    return LingxiResponse.success(body, context == null ? null : context.getTraceId());
  }
}
