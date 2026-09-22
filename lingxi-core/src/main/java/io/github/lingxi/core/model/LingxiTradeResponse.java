// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class LingxiTradeResponse<T> {
  private String tradeCode;
  private String globalSeqNo;
  private String sysSeqNo;
  private String reqSeqNo;
  private String respSeqNo;
  private String origSeqNo;
  private String respCode;
  private String respMsg;
  private T data;
  private long timestamp;
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

  public String getRespSeqNo() {
    return respSeqNo;
  }

  public void setRespSeqNo(String v) {
    respSeqNo = v;
  }

  public String getOrigSeqNo() {
    return origSeqNo;
  }

  public void setOrigSeqNo(String v) {
    origSeqNo = v;
  }

  public String getRespCode() {
    return respCode;
  }

  public void setRespCode(String v) {
    respCode = v;
  }

  public String getRespMsg() {
    return respMsg;
  }

  public void setRespMsg(String v) {
    respMsg = v;
  }

  public T getData() {
    return data;
  }

  public void setData(T v) {
    data = v;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long v) {
    timestamp = v;
  }

  public Map<String, Object> getExt() {
    return ext;
  }

  public void setExt(Map<String, Object> v) {
    ext = v;
  }
}
