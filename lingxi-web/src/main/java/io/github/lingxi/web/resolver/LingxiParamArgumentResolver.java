// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.web.resolver;

import io.github.lingxi.core.annotation.LingxiParam;
import io.github.lingxi.core.context.LingxiContextHolder;
import io.github.lingxi.core.context.LingxiTradeContext;
import io.github.lingxi.core.error.LingxiStandardErrorCode;
import io.github.lingxi.core.exception.LingxiException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolves framework context fields and interceptor-provided extensions for {@code @LingxiParam}.
 */
public class LingxiParamArgumentResolver implements HandlerMethodArgumentResolver {
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(LingxiParam.class);
  }

  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    LingxiParam annotation = parameter.getParameterAnnotation(LingxiParam.class);
    HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
    LingxiTradeContext context =
        (LingxiTradeContext) request.getAttribute(LingxiContextHolder.REQUEST_ATTRIBUTE);
    Object value = null;
    if (context != null) {
      BeanWrapper wrapper = new BeanWrapperImpl(context);
      // Strongly named context properties win; unknown names intentionally fall back to the
      // extension map so applications can introduce values without subclassing the context.
      if (wrapper.isReadableProperty(annotation.value()))
        value = wrapper.getPropertyValue(annotation.value());
      else value = context.getExt().get(annotation.value());
    }
    if (value == null && !annotation.defaultValue().isEmpty()) value = annotation.defaultValue();
    if (value == null && annotation.required())
      throw new LingxiException(
          LingxiStandardErrorCode.VALIDATION_FAILED,
          "Missing Lingxi context parameter: " + annotation.value());
    return value;
  }
}
