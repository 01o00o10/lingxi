// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.error;

public enum LingxiStandardErrorCode implements LingxiErrorCode {
  SUCCESS("0000000", "Success"),
  VALIDATION_FAILED("LX01001", "Request validation failed"),
  UNAUTHENTICATED("LX02001", "Authentication required"),
  FORBIDDEN("LX02002", "Access denied"),
  DUPLICATE_REQUEST("LX03001", "Duplicate request"),
  RATE_LIMITED("LX04001", "Too many requests"),
  INTERNAL_ERROR("LX05001", "Internal server error"),
  SERVICE_UNAVAILABLE("LX05002", "Service unavailable"),
  TIMEOUT("LX05003", "Request timed out");
  private final String code;
  private final String message;

  LingxiStandardErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String code() {
    return code;
  }

  public String message() {
    return message;
  }
}
