// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.autoconfigure.diagnostics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/** Prints a deterministic route and interceptor inventory once startup is complete. */
public class LingxiDiagnosticsReporter implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger log = LoggerFactory.getLogger(LingxiDiagnosticsReporter.class);
  private final LingxiDiagnosticsService diagnosticsService;

  public LingxiDiagnosticsReporter(LingxiDiagnosticsService diagnosticsService) {
    this.diagnosticsService = diagnosticsService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LingxiDiagnosticsSnapshot snapshot = diagnosticsService.snapshot();
    log.info(
        "Lingxi diagnostics: status={}, routes={}, interceptors={}, prefix={}",
        snapshot.getStatus(),
        snapshot.getRouteCount(),
        snapshot.getInterceptorCount(),
        snapshot.getPrefix());
    for (LingxiDiagnosticsSnapshot.Route route : snapshot.getRoutes()) {
      log.info(
          "Lingxi route: methods={} path={} tradeCode={} auth={} handler={}",
          route.getMethods(),
          route.getPath(),
          route.getTradeCode(),
          route.isAuthenticationRequired(),
          route.getImplementation());
    }
    for (LingxiDiagnosticsSnapshot.Interceptor interceptor : snapshot.getInterceptors()) {
      log.info(
          "Lingxi interceptor: order={} type={}", interceptor.getOrder(), interceptor.getName());
    }
    for (String warning : snapshot.getWarnings())
      log.warn("Lingxi diagnostic warning: {}", warning);
  }
}
