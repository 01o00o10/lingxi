// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.autoconfigure;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class LingxiNativeMvcGuardTest {
  @Test
  void rejectsNativeRestControllersInTheConfiguredPackage() {
    AnnotationConfigApplicationContext context = contextWith(NativeController.class);

    assertThatThrownBy(
            () ->
                new LingxiNativeMvcGuard(context, new String[] {getClass().getPackage().getName()})
                    .afterSingletonsInstantiated())
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("API-only mode rejected")
        .hasMessageContaining(NativeController.class.getName());

    context.close();
  }

  @Test
  void allowsOrdinaryLingxiServiceBeans() {
    AnnotationConfigApplicationContext context = contextWith(LingxiService.class);

    assertThatCode(
            () ->
                new LingxiNativeMvcGuard(context, new String[] {getClass().getPackage().getName()})
                    .afterSingletonsInstantiated())
        .doesNotThrowAnyException();

    context.close();
  }

  @Test
  void ignoresNativeControllersOutsideConfiguredPackages() {
    AnnotationConfigApplicationContext context = contextWith(NativeController.class);

    assertThatCode(
            () ->
                new LingxiNativeMvcGuard(context, new String[] {"com.example.business"})
                    .afterSingletonsInstantiated())
        .doesNotThrowAnyException();

    context.close();
  }

  private AnnotationConfigApplicationContext contextWith(Class<?> type) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(type);
    context.refresh();
    return context;
  }

  @RestController
  static class NativeController {
    @GetMapping("/native")
    public String nativeEndpoint() {
      return "native";
    }
  }

  @Service
  static class LingxiService {}
}
