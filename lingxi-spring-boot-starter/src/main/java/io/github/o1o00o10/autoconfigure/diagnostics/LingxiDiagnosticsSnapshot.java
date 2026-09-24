// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.autoconfigure.diagnostics;

import java.util.ArrayList;
import java.util.List;

/** Serializable snapshot of the active Lingxi runtime configuration. */
public class LingxiDiagnosticsSnapshot {
  private String status;
  private long timestamp;
  private String prefix;
  private boolean responseWrapping;
  private boolean authenticationEnabled;
  private boolean asyncEnabled;
  private int routeCount;
  private int interceptorCount;
  private List<Route> routes = new ArrayList<Route>();
  private List<Interceptor> interceptors = new ArrayList<Interceptor>();
  private List<String> warnings = new ArrayList<String>();

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public boolean isResponseWrapping() {
    return responseWrapping;
  }

  public void setResponseWrapping(boolean responseWrapping) {
    this.responseWrapping = responseWrapping;
  }

  public boolean isAuthenticationEnabled() {
    return authenticationEnabled;
  }

  public void setAuthenticationEnabled(boolean authenticationEnabled) {
    this.authenticationEnabled = authenticationEnabled;
  }

  public boolean isAsyncEnabled() {
    return asyncEnabled;
  }

  public void setAsyncEnabled(boolean asyncEnabled) {
    this.asyncEnabled = asyncEnabled;
  }

  public int getRouteCount() {
    return routeCount;
  }

  public void setRouteCount(int routeCount) {
    this.routeCount = routeCount;
  }

  public int getInterceptorCount() {
    return interceptorCount;
  }

  public void setInterceptorCount(int interceptorCount) {
    this.interceptorCount = interceptorCount;
  }

  public List<Route> getRoutes() {
    return routes;
  }

  public void setRoutes(List<Route> routes) {
    this.routes = routes;
  }

  public List<Interceptor> getInterceptors() {
    return interceptors;
  }

  public void setInterceptors(List<Interceptor> interceptors) {
    this.interceptors = interceptors;
  }

  public List<String> getWarnings() {
    return warnings;
  }

  public void setWarnings(List<String> warnings) {
    this.warnings = warnings;
  }

  /** Diagnostic view of one registered API contract. */
  public static class Route {
    private String tradeCode;
    private String path;
    private List<String> methods;
    private String contract;
    private String implementation;
    private boolean authenticationRequired;
    private boolean responseWrapped;

    public String getTradeCode() {
      return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
      this.tradeCode = tradeCode;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public List<String> getMethods() {
      return methods;
    }

    public void setMethods(List<String> methods) {
      this.methods = methods;
    }

    public String getContract() {
      return contract;
    }

    public void setContract(String contract) {
      this.contract = contract;
    }

    public String getImplementation() {
      return implementation;
    }

    public void setImplementation(String implementation) {
      this.implementation = implementation;
    }

    public boolean isAuthenticationRequired() {
      return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
      this.authenticationRequired = authenticationRequired;
    }

    public boolean isResponseWrapped() {
      return responseWrapped;
    }

    public void setResponseWrapped(boolean responseWrapped) {
      this.responseWrapped = responseWrapped;
    }
  }

  /** Diagnostic view of one discovered Lingxi interceptor. */
  public static class Interceptor {
    private String name;
    private int order;

    public Interceptor() {}

    public Interceptor(String name, int order) {
      this.name = name;
      this.order = order;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getOrder() {
      return order;
    }

    public void setOrder(int order) {
      this.order = order;
    }
  }
}
