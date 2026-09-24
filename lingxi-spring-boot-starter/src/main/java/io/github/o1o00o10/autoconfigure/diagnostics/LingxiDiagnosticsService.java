// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.autoconfigure.diagnostics;

import io.github.o1o00o10.autoconfigure.LingxiProperties;
import io.github.o1o00o10.core.interceptor.LingxiHandlerInterceptor;
import io.github.o1o00o10.web.mapping.LingxiApiDefinition;
import io.github.o1o00o10.web.mapping.LingxiApiRegistry;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.aop.support.AopUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/** Builds sanitized diagnostics without exposing secrets or request data. */
public class LingxiDiagnosticsService {
  private static final String DEFAULT_JWT_SECRET = "change-me-change-me-change-me-change-me";
  private final LingxiApiRegistry registry;
  private final List<LingxiHandlerInterceptor> interceptors;
  private final LingxiProperties properties;

  public LingxiDiagnosticsService(
      LingxiApiRegistry registry,
      List<LingxiHandlerInterceptor> interceptors,
      LingxiProperties properties) {
    this.registry = registry;
    this.interceptors = interceptors;
    this.properties = properties;
  }

  public LingxiDiagnosticsSnapshot snapshot() {
    LingxiDiagnosticsSnapshot snapshot = new LingxiDiagnosticsSnapshot();
    List<LingxiDiagnosticsSnapshot.Route> routes = routes();
    List<LingxiDiagnosticsSnapshot.Interceptor> interceptorViews = interceptors();
    snapshot.setStatus(routes.isEmpty() ? "WARN" : "UP");
    snapshot.setTimestamp(System.currentTimeMillis());
    snapshot.setPrefix(properties.getPrefix());
    snapshot.setResponseWrapping(properties.getResponse().isWrap());
    snapshot.setAuthenticationEnabled(properties.getAuth().isEnabled());
    snapshot.setAsyncEnabled(properties.getAsync().isEnabled());
    snapshot.setRouteCount(routes.size());
    snapshot.setInterceptorCount(interceptorViews.size());
    snapshot.setRoutes(routes);
    snapshot.setInterceptors(interceptorViews);
    snapshot.setWarnings(warnings(routes));
    return snapshot;
  }

  private List<LingxiDiagnosticsSnapshot.Route> routes() {
    return registry.definitions().stream()
        .sorted(
            Comparator.comparing(LingxiApiDefinition::getPath)
                .thenComparing(definition -> definition.getApi().tradeCode()))
        .map(this::route)
        .collect(Collectors.toList());
  }

  private LingxiDiagnosticsSnapshot.Route route(LingxiApiDefinition definition) {
    LingxiDiagnosticsSnapshot.Route route = new LingxiDiagnosticsSnapshot.Route();
    route.setTradeCode(definition.getApi().tradeCode());
    route.setPath(definition.getPath());
    route.setMethods(
        Arrays.stream(definition.getApi().method())
            .map(RequestMethod::name)
            .collect(Collectors.toList()));
    route.setContract(methodName(definition.getContractMethod()));
    route.setImplementation(methodName(definition.getImplementationMethod()));
    route.setAuthenticationRequired(definition.getApi().auth());
    route.setResponseWrapped(definition.getApi().wrapResponse());
    return route;
  }

  private List<LingxiDiagnosticsSnapshot.Interceptor> interceptors() {
    return interceptors.stream()
        .sorted(Comparator.comparingInt(LingxiHandlerInterceptor::order))
        .map(
            interceptor ->
                new LingxiDiagnosticsSnapshot.Interceptor(
                    AopUtils.getTargetClass(interceptor).getName(), interceptor.order()))
        .collect(Collectors.toList());
  }

  private List<String> warnings(List<LingxiDiagnosticsSnapshot.Route> routes) {
    List<String> warnings = new ArrayList<String>();
    if (routes.isEmpty()) warnings.add("No Lingxi API routes are registered");
    if (properties.getAuth().isEnabled()
        && DEFAULT_JWT_SECRET.equals(properties.getAuth().getJwt().getSecret())) {
      warnings.add(
          "The default JWT secret is active; configure lingxi.auth.jwt.secret before production");
    }
    if (properties.getDiagnostics().isEndpointEnabled() && !properties.getAuth().isEnabled()) {
      warnings.add("The diagnostics endpoint is exposed while Lingxi authentication is disabled");
    }
    return warnings;
  }

  private String methodName(Method method) {
    return method.getDeclaringClass().getName() + "#" + method.getName();
  }
}
