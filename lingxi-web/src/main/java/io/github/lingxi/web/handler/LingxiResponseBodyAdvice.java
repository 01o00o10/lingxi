// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.web.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lingxi.core.annotation.LingxiApi;
import io.github.lingxi.core.context.LingxiContextHolder;
import io.github.lingxi.core.context.LingxiTradeContext;
import io.github.lingxi.core.interceptor.LingxiInterceptorChain;
import io.github.lingxi.core.model.LingxiResponse;
import io.github.lingxi.core.spi.LingxiResponseWrapper;
import io.github.lingxi.web.interceptor.LingxiWebInterceptor;
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
    if (body instanceof LingxiResponse
        || body instanceof ResponseBodyEmitter
        || body instanceof SseEmitter
        || body instanceof StreamingResponseBody) return body;
    HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
    LingxiApi api = (LingxiApi) servletRequest.getAttribute(LingxiWebInterceptor.API_ATTRIBUTE);
    if (api == null || !api.wrapResponse()) return body;
    LingxiTradeContext context =
        (LingxiTradeContext) servletRequest.getAttribute(LingxiContextHolder.REQUEST_ATTRIBUTE);
    if (context != null && context.getRespSeqNo() == null)
      context.setRespSeqNo("RESP" + System.currentTimeMillis());
    LingxiInterceptorChain chain =
        (LingxiInterceptorChain) servletRequest.getAttribute(LingxiWebInterceptor.CHAIN_ATTRIBUTE);
    if (chain != null) chain.postHandle(context, returnType, body);
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
