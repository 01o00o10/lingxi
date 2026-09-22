// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.web.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LingxiApiRegistry {
  private final Map<String, LingxiApiDefinition> byTradeCode =
      new ConcurrentHashMap<String, LingxiApiDefinition>();

  public void register(LingxiApiDefinition definition) {
    LingxiApiDefinition previous =
        byTradeCode.putIfAbsent(definition.getApi().tradeCode(), definition);
    if (previous != null)
      throw new IllegalStateException(
          "Duplicate Lingxi tradeCode: " + definition.getApi().tradeCode());
  }

  public LingxiApiDefinition get(String tradeCode) {
    return byTradeCode.get(tradeCode);
  }

  public List<LingxiApiDefinition> definitions() {
    return Collections.unmodifiableList(new ArrayList<LingxiApiDefinition>(byTradeCode.values()));
  }
}
