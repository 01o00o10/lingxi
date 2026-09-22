// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class LingxiRequest<T> {
  private Map<String, String> headers = new LinkedHashMap<String, String>();
  private Map<String, String[]> query = new LinkedHashMap<String, String[]>();
  private Map<String, String> path = new LinkedHashMap<String, String>();
  private T body;
  private Map<String, Object> files = new LinkedHashMap<String, Object>();
  private Map<String, Object> metadata = new LinkedHashMap<String, Object>();
  private String traceId;
  private Map<String, Object> ext = new LinkedHashMap<String, Object>();

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public Map<String, String[]> getQuery() {
    return query;
  }

  public void setQuery(Map<String, String[]> query) {
    this.query = query;
  }

  public Map<String, String> getPath() {
    return path;
  }

  public void setPath(Map<String, String> path) {
    this.path = path;
  }

  public T getBody() {
    return body;
  }

  public void setBody(T body) {
    this.body = body;
  }

  public Map<String, Object> getFiles() {
    return files;
  }

  public void setFiles(Map<String, Object> files) {
    this.files = files;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  public Map<String, Object> getExt() {
    return ext;
  }

  public void setExt(Map<String, Object> ext) {
    this.ext = ext;
  }
}
