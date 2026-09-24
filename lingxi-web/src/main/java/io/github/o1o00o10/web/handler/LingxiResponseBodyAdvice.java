// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.web.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.o1o00o10.core.annotation.LingxiApi;
import io.github.o1o00o10.core.context.LingxiContextHolder;
import io.github.o1o00o10.core.context.LingxiTradeContext;
import io.github.o1o00o10.core.interceptor.LingxiInterceptorChain;
import io.github.o1o00o10.core.model.LingxiResponse;
import io.github.o1o00o10.core.spi.LingxiResponseWrapper;
import io.github.o1o00o10.web.interceptor.LingxiWebInterceptor;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/** Applies Lingxi response envelopes after controller invocation and before message conversion. */
@ControllerAdvice
public class LingxiResponseBodyAdvice implements ResponseBodyAdvice<Object> {
  private final LingxiResponseWrapper wrapper;
  private final ObjectMapper objectMapper;
  private final boolean globallyEnabled;

  public LingxiResponseBodyAdvice(
      LingxiResponseWrapper wrapper, ObjectMapper objectMapper, boolean globallyEnabled) {
    this.wrapper = wrapper;
    this.objectMapper = objectMapper;
    this.globallyEnabled = globallyEnabled;
  }

  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return globallyEnabled;
  }

  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
    LingxiTradeContext context =
        (LingxiTradeContext) servletRequest.getAttribute(LingxiContextHolder.REQUEST_ATTRIBUTE);
    LingxiInterceptorChain chain =
        (LingxiInterceptorChain) servletRequest.getAttribute(LingxiWebInterceptor.CHAIN_ATTRIBUTE);
    // This callback represents the Service boundary, so it must run before checking response
    // wrapping. Raw, already-wrapped, SSE, and streaming responses are still valid Service results.
    if (chain != null) chain.postHandle(context, returnType, body);
    if (body instanceof LingxiResponse
        || body instanceof ResponseBodyEmitter
        || body instanceof SseEmitter
        || body instanceof StreamingResponseBody) return body;
    LingxiApi api = (LingxiApi) servletRequest.getAttribute(LingxiWebInterceptor.API_ATTRIBUTE);
    if (api == null || !api.wrapResponse()) return body;
    if (context != null && context.getRespSeqNo() == null)
      context.setRespSeqNo("RESP" + System.currentTimeMillis());
    Object wrapped = wrapper.wrap(body, context);
    if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
      // StringHttpMessageConverter accepts only String. Serialize the envelope explicitly or MVC
      // would either reject LingxiResponse or render its toString() representation.
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      try {
        return objectMapper.writeValueAsString(wrapped);
      } catch (JsonProcessingException ex) {
        throw new IllegalStateException("Cannot serialize Lingxi response", ex);
      }
    }
    return wrapped;
  }
}
