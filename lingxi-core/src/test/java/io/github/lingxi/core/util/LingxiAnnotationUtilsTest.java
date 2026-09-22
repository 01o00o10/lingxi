// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.lingxi.core.annotation.LingxiApi;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class LingxiAnnotationUtilsTest {
  @Test
  void resolvesAnnotationsDeclaredOnlyOnTheContract() throws Exception {
    Method implementation = Service.class.getMethod("query");

    LingxiApi api = LingxiAnnotationUtils.findApi(implementation, Service.class);

    assertThat(api).isNotNull();
    assertThat(api.path()).isEqualTo("/query");
    assertThat(api.tradeCode()).isEqualTo("QUERY_ONE");
  }

  @Test
  void returnsNullForUnannotatedMethods() throws Exception {
    Method method = Service.class.getMethod("plain");
    assertThat(LingxiAnnotationUtils.findApi(method, Service.class)).isNull();
  }

  interface Contract {
    @LingxiApi(path = "/query", tradeCode = "QUERY_ONE")
    String query();
  }

  static class Service implements Contract {
    public String query() {
      return "ok";
    }

    public String plain() {
      return "plain";
    }
  }
}
