// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.core.annotation;

import io.github.o1o00o10.core.interceptor.LingxiInterceptorStage;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface LingxiInterceptor {
  @AliasFor(annotation = Component.class)
  String value() default "";

  int order() default 0;

  LingxiInterceptorStage stage() default LingxiInterceptorStage.ALL;
}
