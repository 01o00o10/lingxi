// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.spi;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.lingxi.core.context.LingxiTradeContext;
import io.github.lingxi.core.model.LingxiResponse;
import org.junit.jupiter.api.Test;

class DefaultLingxiSpiTest {
  @Test
  void validatesTheDefaultTradeCodeFormat() {
    DefaultLingxiTradeCodeValidator validator = new DefaultLingxiTradeCodeValidator();

    assertThat(validator.isValid("PAY_CREATE_01")).isTrue();
    assertThat(validator.isValid("AB")).isFalse();
    assertThat(validator.isValid("pay_create")).isFalse();
    assertThat(validator.isValid("A-B")).isFalse();
    assertThat(validator.isValid(null)).isFalse();
  }

  @Test
  void generatesUniqueKindPrefixedSequences() {
    DefaultLingxiSequenceGenerator generator = new DefaultLingxiSequenceGenerator();
    String first = generator.next("TRACE");
    String second = generator.next("TRACE");

    assertThat(first).startsWith("TRACE");
    assertThat(second).startsWith("TRACE").isNotEqualTo(first);
  }

  @Test
  void wrapsOrdinaryValuesAndPreservesResponses() {
    DefaultLingxiResponseWrapper wrapper = new DefaultLingxiResponseWrapper();
    LingxiTradeContext context = new LingxiTradeContext();
    context.setTraceId("trace-1");
    Object wrapped = wrapper.wrap("value", context);

    assertThat(wrapped).isInstanceOf(LingxiResponse.class);
    LingxiResponse<?> response = (LingxiResponse<?>) wrapped;
    assertThat(response.getData()).isEqualTo("value");
    assertThat(response.getTraceId()).isEqualTo("trace-1");
    assertThat(response.isSuccess()).isTrue();
    assertThat(wrapper.wrap(response, null)).isSameAs(response);
  }
}
