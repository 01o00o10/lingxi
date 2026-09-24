// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.exception;

import io.github.o1o00o10.core.error.LingxiErrorCode;

public class LingxiException extends RuntimeException {
  private final LingxiErrorCode errorCode;

  public LingxiException(LingxiErrorCode errorCode) {
    super(errorCode.message());
    this.errorCode = errorCode;
  }

  public LingxiException(LingxiErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public LingxiException(LingxiErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public LingxiErrorCode getErrorCode() {
    return errorCode;
  }
}
