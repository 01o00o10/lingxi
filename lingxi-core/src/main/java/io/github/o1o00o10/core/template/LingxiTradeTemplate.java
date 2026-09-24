// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.template;

import io.github.o1o00o10.core.context.LingxiTradeContext;
import io.github.o1o00o10.core.error.LingxiStandardErrorCode;
import io.github.o1o00o10.core.exception.LingxiException;
import io.github.o1o00o10.core.spi.LingxiSequenceGenerator;

public class LingxiTradeTemplate {
  private final LingxiSequenceGenerator sequenceGenerator;

  public LingxiTradeTemplate(LingxiSequenceGenerator sequenceGenerator) {
    this.sequenceGenerator = sequenceGenerator;
  }

  public <T> T execute(LingxiTradeContext context, LingxiTradeCallback<T> callback) {
    initialize(context);
    try {
      T result = callback.execute(context);
      context.setRespSeqNo(sequenceGenerator.next("RESP"));
      context.setRespCode(LingxiStandardErrorCode.SUCCESS.code());
      context.setRespMsg(LingxiStandardErrorCode.SUCCESS.message());
      return result;
    } catch (LingxiException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new LingxiException(
          LingxiStandardErrorCode.INTERNAL_ERROR,
          LingxiStandardErrorCode.INTERNAL_ERROR.message(),
          ex);
    }
  }

  public void initialize(LingxiTradeContext context) {
    if (context.getTraceId() == null) context.setTraceId(sequenceGenerator.next("TRACE"));
    if (context.getGlobalSeqNo() == null) context.setGlobalSeqNo(sequenceGenerator.next("GLOBAL"));
    if (context.getSysSeqNo() == null) context.setSysSeqNo(sequenceGenerator.next("SYS"));
    if (context.getReqSeqNo() == null) context.setReqSeqNo(sequenceGenerator.next("REQ"));
    if (context.getOrigSeqNo() == null) context.setOrigSeqNo("");
    context.setStartTime(System.currentTimeMillis());
  }
}
