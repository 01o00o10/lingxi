// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lingxi.autoconfigure.diagnostics.LingxiDiagnosticsEndpoint;
import io.github.lingxi.autoconfigure.diagnostics.LingxiDiagnosticsReporter;
import io.github.lingxi.autoconfigure.diagnostics.LingxiDiagnosticsService;
import io.github.lingxi.core.interceptor.LingxiHandlerInterceptor;
import io.github.lingxi.core.spi.DefaultLingxiResponseWrapper;
import io.github.lingxi.core.spi.DefaultLingxiSequenceGenerator;
import io.github.lingxi.core.spi.DefaultLingxiTradeCodeValidator;
import io.github.lingxi.core.spi.LingxiResponseWrapper;
import io.github.lingxi.core.spi.LingxiSequenceGenerator;
import io.github.lingxi.core.spi.LingxiTradeCodeValidator;
import io.github.lingxi.core.template.LingxiTradeTemplate;
import io.github.lingxi.web.handler.LingxiExceptionHandler;
import io.github.lingxi.web.handler.LingxiResponseBodyAdvice;
import io.github.lingxi.web.interceptor.LingxiWebInterceptor;
import io.github.lingxi.web.mapping.LingxiApiRegistrar;
import io.github.lingxi.web.mapping.LingxiApiRegistry;
import io.github.lingxi.web.resolver.LingxiParamArgumentResolver;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DispatcherServlet.class, RequestMappingHandlerMapping.class})
@ConditionalOnProperty(
    prefix = "lingxi",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(LingxiProperties.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
/** Core MVC auto-configuration. Every strategy bean can be replaced by an application bean. */
public class LingxiAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public LingxiSequenceGenerator lingxiSequenceGenerator() {
    return new DefaultLingxiSequenceGenerator();
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiTradeCodeValidator lingxiTradeCodeValidator() {
    return new DefaultLingxiTradeCodeValidator();
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiResponseWrapper lingxiResponseWrapper() {
    return new DefaultLingxiResponseWrapper();
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiTradeTemplate lingxiTradeTemplate(LingxiSequenceGenerator generator) {
    return new LingxiTradeTemplate(generator);
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiApiRegistry lingxiApiRegistry() {
    return new LingxiApiRegistry();
  }

  @Bean
  public LingxiApiRegistrar lingxiApiRegistrar(
      ConfigurableApplicationContext context,
      @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping,
      LingxiApiRegistry registry,
      LingxiTradeCodeValidator validator,
      LingxiProperties properties) {
    return new LingxiApiRegistrar(
        context, mapping, registry, validator, properties.getPrefix(), properties.getBasePackage());
  }

  @Bean
  @ConditionalOnProperty(prefix = "lingxi", name = "enforce-api-only", havingValue = "true")
  public LingxiNativeMvcGuard lingxiNativeMvcGuard(
      ApplicationContext context, LingxiProperties properties) {
    return new LingxiNativeMvcGuard(context, properties.getBasePackage());
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiWebInterceptor lingxiWebInterceptor(
      ApplicationContext context,
      List<LingxiHandlerInterceptor> interceptors,
      LingxiSequenceGenerator generator) {
    return new LingxiWebInterceptor(context, interceptors, generator);
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiParamArgumentResolver lingxiParamArgumentResolver() {
    return new LingxiParamArgumentResolver();
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiResponseBodyAdvice lingxiResponseBodyAdvice(
      LingxiResponseWrapper wrapper, ObjectMapper mapper, LingxiProperties properties) {
    return new LingxiResponseBodyAdvice(wrapper, mapper, properties.getResponse().isWrap());
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiExceptionHandler lingxiExceptionHandler() {
    return new LingxiExceptionHandler();
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiDiagnosticsService lingxiDiagnosticsService(
      LingxiApiRegistry registry,
      List<LingxiHandlerInterceptor> interceptors,
      LingxiProperties properties) {
    return new LingxiDiagnosticsService(registry, interceptors, properties);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(
      prefix = "lingxi.diagnostics",
      name = "log-on-startup",
      havingValue = "true",
      matchIfMissing = true)
  public LingxiDiagnosticsReporter lingxiDiagnosticsReporter(LingxiDiagnosticsService service) {
    return new LingxiDiagnosticsReporter(service);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(
      prefix = "lingxi.diagnostics",
      name = "endpoint-enabled",
      havingValue = "true")
  public LingxiDiagnosticsEndpoint lingxiDiagnosticsEndpoint(LingxiDiagnosticsService service) {
    return new LingxiDiagnosticsEndpoint(service);
  }

  @Bean
  public WebMvcConfigurer lingxiWebMvcConfigurer(
      LingxiWebInterceptor interceptor,
      LingxiParamArgumentResolver resolver,
      LingxiProperties properties) {
    return new WebMvcConfigurer() {
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
        if (properties.getInterceptor().isEnabled()) registry.addInterceptor(interceptor);
      }

      @Override
      public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
      }

      @Override
      public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        if (properties.getAsync().isEnabled())
          configurer.setDefaultTimeout(properties.getAsync().getDefaultTimeout().toMillis());
      }
    };
  }
}
