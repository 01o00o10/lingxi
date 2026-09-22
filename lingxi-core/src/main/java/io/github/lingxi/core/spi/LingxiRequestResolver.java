// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.spi;

import io.github.lingxi.core.model.LingxiRequest;
import javax.servlet.http.HttpServletRequest;

public interface LingxiRequestResolver {
  LingxiRequest<?> resolve(HttpServletRequest request);
}
