// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.middleware.spi;

import io.github.lingxi.core.interceptor.LingxiHandlerInterceptor;

/** Common marker for Nacos, Sentinel, Seata, MQ and metrics adapters. */
public interface LingxiMiddlewareInterceptor extends LingxiHandlerInterceptor {
  String middleware();
}
