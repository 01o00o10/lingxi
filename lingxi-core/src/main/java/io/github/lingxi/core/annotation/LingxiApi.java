// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.annotation;

import io.github.lingxi.core.interceptor.LingxiHandlerInterceptor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseBody
public @interface LingxiApi {
  @AliasFor("path")
  String value() default "";

  @AliasFor("value")
  String path() default "";

  RequestMethod[] method() default {RequestMethod.POST};

  String[] consumes() default {};

  String[] produces() default {"application/json"};

  String version() default "v1";

  String desc() default "";

  String tradeCode();

  long timeout() default -1L;

  boolean auth() default true;

  Class<? extends LingxiHandlerInterceptor>[] interceptors() default {};

  String template() default "";

  boolean wrapResponse() default true;
}
