// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.o1o00o10.web.mapping;

import io.github.o1o00o10.core.annotation.LingxiApi;
import io.github.o1o00o10.core.spi.LingxiTradeCodeValidator;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Discovers Lingxi contracts after all regular singleton beans exist and registers their methods
 * with Spring MVC's native handler mapping.
 *
 * <p>The contract method, rather than the implementation method, is deliberately registered so
 * Spring MVC reads parameter annotations from the public API interface.
 */
public class LingxiApiRegistrar implements SmartInitializingSingleton {
  private static final Logger log = LoggerFactory.getLogger(LingxiApiRegistrar.class);
  private final ConfigurableApplicationContext applicationContext;
  private final RequestMappingHandlerMapping handlerMapping;
  private final LingxiApiRegistry registry;
  private final LingxiTradeCodeValidator tradeCodeValidator;
  private final String prefix;
  private final String[] basePackages;

  public LingxiApiRegistrar(
      ConfigurableApplicationContext applicationContext,
      RequestMappingHandlerMapping handlerMapping,
      LingxiApiRegistry registry,
      LingxiTradeCodeValidator tradeCodeValidator,
      String prefix,
      String[] basePackages) {
    this.applicationContext = applicationContext;
    this.handlerMapping = handlerMapping;
    this.registry = registry;
    this.tradeCodeValidator = tradeCodeValidator;
    this.prefix = normalize(prefix);
    this.basePackages = basePackages == null ? new String[0] : basePackages;
  }

  @Override
  public void afterSingletonsInstantiated() {
    // Waiting for this lifecycle phase lets us inspect final AOP proxies and avoids instantiating
    // application beans while their definitions are still being processed.
    Set<String> registered = new HashSet<String>();
    for (String beanName : applicationContext.getBeanFactory().getBeanDefinitionNames()) {
      Class<?> beanType = applicationContext.getType(beanName, false);
      if (beanType == null || !inBasePackage(beanType) || isInfrastructure(beanType)) continue;
      Object bean;
      try {
        bean = applicationContext.getBean(beanName);
      } catch (RuntimeException ex) {
        log.debug("Skipping unavailable bean {}", beanName, ex);
        continue;
      }
      Class<?> targetClass = AopUtils.getTargetClass(bean);
      // Include inherited interfaces so a shared parent contract can declare API methods once.
      for (Class<?> contract : ClassUtils.getAllInterfacesForClassAsSet(targetClass)) {
        scanContract(beanName, bean, targetClass, contract, registered);
      }
    }
    log.info("Registered {} Lingxi API mappings", registry.definitions().size());
  }

  private void scanContract(
      String beanName,
      Object bean,
      Class<?> targetClass,
      Class<?> contract,
      Set<String> registered) {
    for (Method contractMethod : contract.getMethods()) {
      LingxiApi api = AnnotatedElementUtils.findMergedAnnotation(contractMethod, LingxiApi.class);
      if (api == null) continue;
      String identity = beanName + '#' + contractMethod.toGenericString();
      if (!registered.add(identity)) continue;
      if (!tradeCodeValidator.isValid(api.tradeCode())) {
        throw new IllegalStateException(
            "Invalid Lingxi tradeCode '" + api.tradeCode() + "' on " + contractMethod);
      }
      Method implementationMethod =
          ClassUtils.getMethod(
              targetClass, contractMethod.getName(), contractMethod.getParameterTypes());
      implementationMethod = BridgeMethodResolver.findBridgedMethod(implementationMethod);
      String path = buildPath(api);
      RequestMappingInfo.Builder builder = RequestMappingInfo.paths(path).methods(api.method());
      if (api.consumes().length > 0) builder.consumes(api.consumes());
      if (api.produces().length > 0) builder.produces(api.produces());
      RequestMappingInfo mapping =
          builder
              .mappingName("lingxi:" + api.tradeCode())
              .options(handlerMapping.getBuilderConfiguration())
              .build();
      LingxiApiDefinition definition =
          new LingxiApiDefinition(beanName, bean, contractMethod, implementationMethod, api, path);
      registry.register(definition);
      // Register against the interface method. Spring invokes it on the implementation bean while
      // retaining MVC annotations declared by the contract.
      handlerMapping.registerMapping(mapping, bean, contractMethod);
      log.info(
          "Mapped Lingxi API {} {} to {}", api.tradeCode(), path, contractMethod.toGenericString());
    }
  }

  private boolean inBasePackage(Class<?> type) {
    if (basePackages.length == 0) return true;
    String name = type.getName();
    for (String basePackage : basePackages)
      if (StringUtils.hasText(basePackage) && name.startsWith(basePackage + ".")) return true;
    return false;
  }

  private boolean isInfrastructure(Class<?> type) {
    String name = type.getName();
    return Modifier.isAbstract(type.getModifiers()) || name.startsWith("org.springframework.");
  }

  private String buildPath(LingxiApi api) {
    return normalize(prefix + '/' + normalize(api.version()) + '/' + normalize(api.path()));
  }

  private static String normalize(String value) {
    String path = value == null ? "" : value.trim().replaceAll("/{2,}", "/");
    if (!path.startsWith("/")) path = "/" + path;
    if (path.length() > 1 && path.endsWith("/")) path = path.substring(0, path.length() - 1);
    return path;
  }
}
