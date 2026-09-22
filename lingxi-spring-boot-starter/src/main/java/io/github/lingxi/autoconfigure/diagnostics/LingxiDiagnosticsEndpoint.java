// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.autoconfigure.diagnostics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read-only HTTP endpoint for inspecting the active Lingxi runtime. */
@RestController
public class LingxiDiagnosticsEndpoint {
  private final LingxiDiagnosticsService diagnosticsService;

  public LingxiDiagnosticsEndpoint(LingxiDiagnosticsService diagnosticsService) {
    this.diagnosticsService = diagnosticsService;
  }

  @GetMapping("${lingxi.diagnostics.path:/lingxi/diagnostics}")
  public LingxiDiagnosticsSnapshot diagnostics() {
    return diagnosticsService.snapshot();
  }
}
