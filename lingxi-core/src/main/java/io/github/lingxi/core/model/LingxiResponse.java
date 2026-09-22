// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.model;

import io.github.lingxi.core.error.LingxiErrorCode;
import io.github.lingxi.core.error.LingxiStandardErrorCode;
import java.util.LinkedHashMap;
import java.util.Map;

public class LingxiResponse<T> {
  private String code;
  private String message;
  private T data;
  private String traceId;
  private long timestamp;
  private boolean success;
  private Map<String, Object> ext = new LinkedHashMap<String, Object>();

  public static <T> LingxiResponse<T> success(T data, String traceId) {
    return of(LingxiStandardErrorCode.SUCCESS, data, traceId, true);
  }

  public static <T> LingxiResponse<T> failure(
      LingxiErrorCode error, String message, String traceId) {
    LingxiResponse<T> response = of(error, null, traceId, false);
    if (message != null && !message.isEmpty()) response.setMessage(message);
    return response;
  }

  private static <T> LingxiResponse<T> of(
      LingxiErrorCode error, T data, String traceId, boolean success) {
    LingxiResponse<T> response = new LingxiResponse<T>();
    response.code = error.code();
    response.message = error.message();
    response.data = data;
    response.traceId = traceId;
    response.timestamp = System.currentTimeMillis();
    response.success = success;
    return response;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public Map<String, Object> getExt() {
    return ext;
  }

  public void setExt(Map<String, Object> ext) {
    this.ext = ext;
  }
}
