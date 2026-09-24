// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.interceptor;

/** Lifecycle phase in which a Lingxi interceptor participates. */
public enum LingxiInterceptorStage {
  /** Runs immediately before the registered Service method. Returning false rejects the request. */
  BEFORE_SERVICE,

  /** Runs immediately after the Service method returns a result, before response conversion. */
  AFTER_SERVICE,

  /** Runs when the Service method or request processing fails. */
  ON_EXCEPTION,

  /** Runs exactly once when request processing is complete. */
  AFTER_COMPLETION,

  /** Backward-compatible mode that participates in every lifecycle callback. */
  ALL,

  /**
   * @deprecated Use {@link #BEFORE_SERVICE}.
   */
  @Deprecated
  PRE_HANDLE,

  /**
   * @deprecated Use {@link #AFTER_SERVICE}.
   */
  @Deprecated
  POST_HANDLE,

  /**
   * @deprecated Use {@link #ON_EXCEPTION}.
   */
  @Deprecated
  EXCEPTION;

  public LingxiInterceptorStage normalized() {
    if (this == PRE_HANDLE) return BEFORE_SERVICE;
    if (this == POST_HANDLE) return AFTER_SERVICE;
    if (this == EXCEPTION) return ON_EXCEPTION;
    return this;
  }
}
