// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.web.handler;

import io.github.o1o00o10.core.context.LingxiContextHolder;
import io.github.o1o00o10.core.context.LingxiTradeContext;
import io.github.o1o00o10.core.error.LingxiErrorCode;
import io.github.o1o00o10.core.error.LingxiStandardErrorCode;
import io.github.o1o00o10.core.exception.LingxiException;
import io.github.o1o00o10.core.interceptor.LingxiInterceptorChain;
import io.github.o1o00o10.core.model.LingxiResponse;
import io.github.o1o00o10.web.interceptor.LingxiWebInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LingxiExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(LingxiExceptionHandler.class);

  @ExceptionHandler(LingxiException.class)
  public ResponseEntity<LingxiResponse<Void>> handleLingxi(
      LingxiException ex, HttpServletRequest request) {
    HttpStatus status = status(ex.getErrorCode());
    complete(request, ex);
    return ResponseEntity.status(status).body(failure(ex.getErrorCode(), ex.getMessage(), request));
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    BindException.class,
    ConstraintViolationException.class,
    MissingServletRequestParameterException.class
  })
  public ResponseEntity<LingxiResponse<Void>> handleValidation(
      Exception ex, HttpServletRequest request) {
    complete(request, ex);
    return ResponseEntity.badRequest()
        .body(
            failure(
                LingxiStandardErrorCode.VALIDATION_FAILED, "Request validation failed", request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<LingxiResponse<Void>> handleUnexpected(
      Exception ex, HttpServletRequest request) {
    log.error("Unhandled Lingxi request failure, traceId={}", traceId(request), ex);
    complete(request, ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            failure(
                LingxiStandardErrorCode.INTERNAL_ERROR,
                LingxiStandardErrorCode.INTERNAL_ERROR.message(),
                request));
  }

  private LingxiResponse<Void> failure(
      LingxiErrorCode code, String message, HttpServletRequest request) {
    return LingxiResponse.failure(code, message, traceId(request));
  }

  private String traceId(HttpServletRequest request) {
    LingxiTradeContext context =
        (LingxiTradeContext) request.getAttribute(LingxiContextHolder.REQUEST_ATTRIBUTE);
    return context == null ? null : context.getTraceId();
  }

  private void complete(HttpServletRequest request, Exception ex) {
    LingxiInterceptorChain chain =
        (LingxiInterceptorChain) request.getAttribute(LingxiWebInterceptor.CHAIN_ATTRIBUTE);
    LingxiTradeContext context =
        (LingxiTradeContext) request.getAttribute(LingxiContextHolder.REQUEST_ATTRIBUTE);
    if (chain != null) chain.complete(context, request, ex);
  }

  private HttpStatus status(LingxiErrorCode code) {
    if (code == LingxiStandardErrorCode.UNAUTHENTICATED) return HttpStatus.UNAUTHORIZED;
    if (code == LingxiStandardErrorCode.FORBIDDEN) return HttpStatus.FORBIDDEN;
    if (code == LingxiStandardErrorCode.DUPLICATE_REQUEST) return HttpStatus.CONFLICT;
    if (code == LingxiStandardErrorCode.RATE_LIMITED) return HttpStatus.TOO_MANY_REQUESTS;
    if (code == LingxiStandardErrorCode.TIMEOUT) return HttpStatus.GATEWAY_TIMEOUT;
    if (code == LingxiStandardErrorCode.VALIDATION_FAILED) return HttpStatus.BAD_REQUEST;
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
