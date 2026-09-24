// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.context;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class LingxiTradeContext {
  private String tradeCode;
  private String traceId;
  private String globalSeqNo;
  private String sysSeqNo;
  private String reqSeqNo;
  private String respSeqNo;
  private String origSeqNo;
  private String channel;
  private String branchNo;
  private String tellerNo;
  private String userId;
  private String accountNo;
  private String currency;
  private BigDecimal amount;
  private String nonce;
  private String sign;
  private String idempotentNo;
  private String respCode;
  private String respMsg;
  private long startTime;
  private final Map<String, Object> ext = new LinkedHashMap<String, Object>();

  public String getTradeCode() {
    return tradeCode;
  }

  public void setTradeCode(String tradeCode) {
    this.tradeCode = tradeCode;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  public String getGlobalSeqNo() {
    return globalSeqNo;
  }

  public void setGlobalSeqNo(String globalSeqNo) {
    this.globalSeqNo = globalSeqNo;
  }

  public String getSysSeqNo() {
    return sysSeqNo;
  }

  public void setSysSeqNo(String sysSeqNo) {
    this.sysSeqNo = sysSeqNo;
  }

  public String getReqSeqNo() {
    return reqSeqNo;
  }

  public void setReqSeqNo(String reqSeqNo) {
    this.reqSeqNo = reqSeqNo;
  }

  public String getRespSeqNo() {
    return respSeqNo;
  }

  public void setRespSeqNo(String respSeqNo) {
    this.respSeqNo = respSeqNo;
  }

  public String getOrigSeqNo() {
    return origSeqNo;
  }

  public void setOrigSeqNo(String origSeqNo) {
    this.origSeqNo = origSeqNo;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getBranchNo() {
    return branchNo;
  }

  public void setBranchNo(String branchNo) {
    this.branchNo = branchNo;
  }

  public String getTellerNo() {
    return tellerNo;
  }

  public void setTellerNo(String tellerNo) {
    this.tellerNo = tellerNo;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(String accountNo) {
    this.accountNo = accountNo;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getNonce() {
    return nonce;
  }

  public void setNonce(String nonce) {
    this.nonce = nonce;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getIdempotentNo() {
    return idempotentNo;
  }

  public void setIdempotentNo(String idempotentNo) {
    this.idempotentNo = idempotentNo;
  }

  public String getRespCode() {
    return respCode;
  }

  public void setRespCode(String respCode) {
    this.respCode = respCode;
  }

  public String getRespMsg() {
    return respMsg;
  }

  public void setRespMsg(String respMsg) {
    this.respMsg = respMsg;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public Map<String, Object> getExt() {
    return ext;
  }
}
