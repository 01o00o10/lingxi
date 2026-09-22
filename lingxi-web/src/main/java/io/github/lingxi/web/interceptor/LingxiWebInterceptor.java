// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.web.interceptor;

import io.github.lingxi.core.annotation.LingxiApi;
import io.github.lingxi.core.context.LingxiContextHolder;
import io.github.lingxi.core.context.LingxiTradeContext;
import io.github.lingxi.core.error.LingxiStandardErrorCode;
import io.github.lingxi.core.exception.LingxiException;
import io.github.lingxi.core.interceptor.LingxiHandlerInterceptor;
import io.github.lingxi.core.interceptor.LingxiInterceptorChain;
import io.github.lingxi.core.spi.LingxiSequenceGenerator;
import io.github.lingxi.core.util.LingxiAnnotationUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/** Bridges the Servlet request lifecycle to Lingxi context and interceptor lifecycle callbacks. */
public class LingxiWebInterceptor implements AsyncHandlerInterceptor {
  public static final String CHAIN_ATTRIBUTE = LingxiWebInterceptor.class.getName() + ".CHAIN";
  public static final String API_ATTRIBUTE = LingxiWebInterceptor.class.getName() + ".API";
  private final ApplicationContext applicationContext;
  private final List<LingxiHandlerInterceptor> globalInterceptors;
  private final LingxiSequenceGenerator sequenceGenerator;

  public LingxiWebInterceptor(
      ApplicationContext applicationContext,
      List<LingxiHandlerInterceptor> globalInterceptors,
      LingxiSequenceGenerator sequenceGenerator) {
    this.applicationContext = applicationContext;
    this.globalInterceptors = globalInterceptors;
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!(handler instanceof HandlerMethod)) return true;
    HandlerMethod method = (HandlerMethod) handler;
    LingxiApi api = LingxiAnnotationUtils.findApi(method.getMethod(), method.getBeanType());
    if (api == null) return true;
    LingxiTradeContext context = createContext(request, api);
    // Request attributes survive async redispatch; ThreadLocal state does not cross executor
    // threads and is used only while the current servlet thread owns the request.
    request.setAttribute(API_ATTRIBUTE, api);
    LingxiContextHolder.set(context);
    request.setAttribute(LingxiContextHolder.REQUEST_ATTRIBUTE, context);
    LingxiInterceptorChain chain = new LingxiInterceptorChain(resolveInterceptors(api));
    request.setAttribute(CHAIN_ATTRIBUTE, chain);
    if (!chain.preHandle(context, handler))
      throw new LingxiException(
          LingxiStandardErrorCode.FORBIDDEN, "Request rejected by interceptor");
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView) {}

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    try {
      LingxiInterceptorChain chain = (LingxiInterceptorChain) request.getAttribute(CHAIN_ATTRIBUTE);
      LingxiTradeContext context =
          (LingxiTradeContext) request.getAttribute(LingxiContextHolder.REQUEST_ATTRIBUTE);
      if (chain != null) chain.complete(context, handler, ex);
    } finally {
      LingxiContextHolder.clear();
    }
  }

  @Override
  public void afterConcurrentHandlingStarted(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    // The container thread is about to return to the pool. Keeping the context here could leak one
    // request's identity into an unrelated request handled by the same thread.
    LingxiContextHolder.clear();
  }

  private LingxiTradeContext createContext(HttpServletRequest request, LingxiApi api) {
    LingxiTradeContext context = new LingxiTradeContext();
    context.setTradeCode(api.tradeCode());
    context.setTraceId(value(request, "X-Trace-Id", sequenceGenerator.next("TRACE")));
    context.setGlobalSeqNo(value(request, "X-Global-Seq-No", sequenceGenerator.next("GLOBAL")));
    context.setSysSeqNo(value(request, "X-Sys-Seq-No", sequenceGenerator.next("SYS")));
    context.setReqSeqNo(value(request, "X-Req-Seq-No", sequenceGenerator.next("REQ")));
    context.setOrigSeqNo(value(request, "X-Orig-Seq-No", ""));
    context.setChannel(request.getHeader("X-Channel"));
    context.setBranchNo(request.getHeader("X-Branch-No"));
    context.setTellerNo(request.getHeader("X-Teller-No"));
    context.setNonce(request.getHeader("X-Nonce"));
    context.setSign(request.getHeader("X-Sign"));
    context.setIdempotentNo(request.getHeader("X-Idempotent-No"));
    context.setStartTime(System.currentTimeMillis());
    return context;
  }

  private List<LingxiHandlerInterceptor> resolveInterceptors(LingxiApi api) {
    // A method-level interceptor replaces a global interceptor of the same concrete class instead
    // of running it twice. LinkedHashMap keeps bean discovery order until the chain sorts by order.
    Map<Class<?>, LingxiHandlerInterceptor> unique =
        new LinkedHashMap<Class<?>, LingxiHandlerInterceptor>();
    for (LingxiHandlerInterceptor interceptor : globalInterceptors)
      unique.put(interceptor.getClass(), interceptor);
    for (Class<? extends LingxiHandlerInterceptor> type : api.interceptors())
      unique.put(type, applicationContext.getBean(type));
    return new ArrayList<LingxiHandlerInterceptor>(unique.values());
  }

  private String value(HttpServletRequest request, String header, String fallback) {
    String value = request.getHeader(header);
    return value == null || value.trim().isEmpty() ? fallback : value;
  }
}
