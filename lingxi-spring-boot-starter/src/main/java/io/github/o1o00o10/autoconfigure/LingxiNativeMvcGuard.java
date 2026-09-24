// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.autoconfigure;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Fails fast when API-only mode finds a business Bean using native Spring MVC mappings. */
public class LingxiNativeMvcGuard implements SmartInitializingSingleton {
  private final ApplicationContext applicationContext;
  private final String[] basePackages;

  public LingxiNativeMvcGuard(ApplicationContext applicationContext, String[] basePackages) {
    this.applicationContext = applicationContext;
    this.basePackages = basePackages == null ? new String[0] : basePackages;
  }

  @Override
  public void afterSingletonsInstantiated() {
    for (String beanName : applicationContext.getBeanDefinitionNames()) {
      Class<?> type = applicationContext.getType(beanName, false);
      if (type == null || !inBasePackage(type)) continue;
      if (hasNativeController(type)) reject(beanName, type, type);
      for (Method method : type.getMethods()) {
        if (AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class)) {
          reject(beanName, type, method);
        }
      }
    }
  }

  private boolean hasNativeController(Class<?> type) {
    return AnnotatedElementUtils.hasAnnotation(type, Controller.class)
        || AnnotatedElementUtils.hasAnnotation(type, RestController.class)
        || AnnotatedElementUtils.hasAnnotation(type, RequestMapping.class);
  }

  private boolean inBasePackage(Class<?> type) {
    if (basePackages.length == 0) return true;
    String name = type.getName();
    return Arrays.stream(basePackages)
        .filter(StringUtils::hasText)
        .anyMatch(basePackage -> name.startsWith(basePackage + "."));
  }

  private void reject(String beanName, Class<?> type, Object element) {
    throw new IllegalStateException(
        "Lingxi API-only mode rejected native Spring MVC mapping on "
            + beanName
            + " ("
            + type.getName()
            + "). Use @LingxiApi on an interface method instead: "
            + element);
  }
}
