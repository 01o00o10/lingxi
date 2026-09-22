// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.lingxi.core.context.LingxiTradeContext;
import io.github.lingxi.core.error.LingxiStandardErrorCode;
import io.github.lingxi.core.exception.LingxiException;
import io.github.lingxi.core.spi.LingxiSequenceGenerator;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class LingxiTradeTemplateTest {
  @Test
  void initializesEveryRequiredSequenceAndRecordsSuccess() {
    AtomicInteger counter = new AtomicInteger();
    LingxiSequenceGenerator generator = kind -> kind + '-' + counter.incrementAndGet();
    LingxiTradeTemplate template = new LingxiTradeTemplate(generator);
    LingxiTradeContext context = new LingxiTradeContext();
    context.setTradeCode("PAY_CREATE");
    String result = template.execute(context, current -> current.getTradeCode());
    assertThat(result).isEqualTo("PAY_CREATE");
    assertThat(context.getTraceId()).startsWith("TRACE-");
    assertThat(context.getGlobalSeqNo()).startsWith("GLOBAL-");
    assertThat(context.getSysSeqNo()).startsWith("SYS-");
    assertThat(context.getReqSeqNo()).startsWith("REQ-");
    assertThat(context.getRespSeqNo()).startsWith("RESP-");
    assertThat(context.getOrigSeqNo()).isNotNull();
    assertThat(context.getRespCode()).isEqualTo("0000000");
  }

  @Test
  void preservesCallerProvidedSequences() {
    LingxiTradeContext context = new LingxiTradeContext();
    context.setTraceId("trace");
    context.setGlobalSeqNo("global");
    context.setSysSeqNo("system");
    context.setReqSeqNo("request");
    context.setOrigSeqNo("original");
    new LingxiTradeTemplate(kind -> "generated").initialize(context);

    assertThat(context.getTraceId()).isEqualTo("trace");
    assertThat(context.getGlobalSeqNo()).isEqualTo("global");
    assertThat(context.getSysSeqNo()).isEqualTo("system");
    assertThat(context.getReqSeqNo()).isEqualTo("request");
    assertThat(context.getOrigSeqNo()).isEqualTo("original");
    assertThat(context.getStartTime()).isPositive();
  }

  @Test
  void preservesLingxiExceptionsAndWrapsOtherFailures() {
    LingxiTradeTemplate template = new LingxiTradeTemplate(kind -> kind);
    LingxiException expected = new LingxiException(LingxiStandardErrorCode.FORBIDDEN);

    assertThatThrownBy(
            () ->
                template.execute(
                    new LingxiTradeContext(),
                    context -> {
                      throw expected;
                    }))
        .isSameAs(expected);
    assertThatThrownBy(
            () ->
                template.execute(
                    new LingxiTradeContext(),
                    context -> {
                      throw new Exception("database detail");
                    }))
        .isInstanceOf(LingxiException.class)
        .hasMessage("Internal server error")
        .hasCauseInstanceOf(Exception.class);
  }
}
