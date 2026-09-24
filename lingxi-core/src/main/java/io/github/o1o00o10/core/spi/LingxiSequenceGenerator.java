// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.spi;

public interface LingxiSequenceGenerator {
  String next(String kind);
}
