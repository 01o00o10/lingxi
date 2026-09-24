// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.web.mapping;

import io.github.o1o00o10.core.annotation.LingxiApi;
import java.lang.reflect.Method;

public class LingxiApiDefinition {
  private final String beanName;
  private final Object bean;
  private final Method contractMethod;
  private final Method implementationMethod;
  private final LingxiApi api;
  private final String path;

  public LingxiApiDefinition(
      String beanName,
      Object bean,
      Method contractMethod,
      Method implementationMethod,
      LingxiApi api,
      String path) {
    this.beanName = beanName;
    this.bean = bean;
    this.contractMethod = contractMethod;
    this.implementationMethod = implementationMethod;
    this.api = api;
    this.path = path;
  }

  public String getBeanName() {
    return beanName;
  }

  public Object getBean() {
    return bean;
  }

  public Method getContractMethod() {
    return contractMethod;
  }

  public Method getImplementationMethod() {
    return implementationMethod;
  }

  public LingxiApi getApi() {
    return api;
  }

  public String getPath() {
    return path;
  }
}
