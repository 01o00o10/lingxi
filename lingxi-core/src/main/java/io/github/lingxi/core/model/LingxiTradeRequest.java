// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class LingxiTradeRequest<T> {
  private String tradeCode;
  private String globalSeqNo;
  private String sysSeqNo;
  private String reqSeqNo;
  private String origSeqNo;
  private String channel;
  private String branchNo;
  private String tellerNo;
  private String userId;
  private String accountNo;
  private String currency;
  private BigDecimal amount;
  private long timestamp;
  private String nonce;
  private String sign;
  private String idempotentNo;
  private T body;
  private Map<String, Object> ext = new LinkedHashMap<String, Object>();

  public String getTradeCode() {
    return tradeCode;
  }

  public void setTradeCode(String v) {
    tradeCode = v;
  }

  public String getGlobalSeqNo() {
    return globalSeqNo;
  }

  public void setGlobalSeqNo(String v) {
    globalSeqNo = v;
  }

  public String getSysSeqNo() {
    return sysSeqNo;
  }

  public void setSysSeqNo(String v) {
    sysSeqNo = v;
  }

  public String getReqSeqNo() {
    return reqSeqNo;
  }

  public void setReqSeqNo(String v) {
    reqSeqNo = v;
  }

  public String getOrigSeqNo() {
    return origSeqNo;
  }

  public void setOrigSeqNo(String v) {
    origSeqNo = v;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String v) {
    channel = v;
  }

  public String getBranchNo() {
    return branchNo;
  }

  public void setBranchNo(String v) {
    branchNo = v;
  }

  public String getTellerNo() {
    return tellerNo;
  }

  public void setTellerNo(String v) {
    tellerNo = v;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String v) {
    userId = v;
  }

  public String getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(String v) {
    accountNo = v;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String v) {
    currency = v;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal v) {
    amount = v;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long v) {
    timestamp = v;
  }

  public String getNonce() {
    return nonce;
  }

  public void setNonce(String v) {
    nonce = v;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String v) {
    sign = v;
  }

  public String getIdempotentNo() {
    return idempotentNo;
  }

  public void setIdempotentNo(String v) {
    idempotentNo = v;
  }

  public T getBody() {
    return body;
  }

  public void setBody(T v) {
    body = v;
  }

  public Map<String, Object> getExt() {
    return ext;
  }

  public void setExt(Map<String, Object> v) {
    ext = v;
  }
}
