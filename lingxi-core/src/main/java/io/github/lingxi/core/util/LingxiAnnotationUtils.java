// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.core.util;

import io.github.lingxi.core.annotation.LingxiApi;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

public final class LingxiAnnotationUtils {
  private LingxiAnnotationUtils() {}

  public static LingxiApi findApi(Method method, Class<?> containingClass) {
    return findMethodAnnotation(method, containingClass, LingxiApi.class);
  }

  public static <A extends Annotation> A findMethodAnnotation(
      Method method, Class<?> containingClass, Class<A> annotationType) {
    A direct = AnnotatedElementUtils.findMergedAnnotation(method, annotationType);
    if (direct != null) return direct;
    for (Class<?> contract : ClassUtils.getAllInterfacesForClassAsSet(containingClass)) {
      try {
        Method candidate = contract.getMethod(method.getName(), method.getParameterTypes());
        A annotation = AnnotatedElementUtils.findMergedAnnotation(candidate, annotationType);
        if (annotation != null) return annotation;
      } catch (NoSuchMethodException ignored) {
      }
    }
    return null;
  }
}
