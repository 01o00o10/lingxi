// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.autoconfigure;

import io.github.lingxi.security.auth.DefaultLingxiLoginService;
import io.github.lingxi.security.auth.LingxiAuthenticationService;
import io.github.lingxi.security.auth.LingxiLoginApi;
import io.github.lingxi.security.interceptor.LingxiAuthInterceptor;
import io.github.lingxi.security.jwt.LingxiJwtAuthenticationFilter;
import io.github.lingxi.security.jwt.LingxiJwtTokenProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SecurityFilterChain.class, LingxiJwtTokenProvider.class})
@ConditionalOnProperty(
    prefix = "lingxi.auth",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
/** Optional stateless JWT security that backs off when the application defines its own beans. */
public class LingxiSecurityAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public LingxiJwtTokenProvider lingxiJwtTokenProvider(LingxiProperties properties) {
    LingxiProperties.Auth.Jwt jwt = properties.getAuth().getJwt();
    return new LingxiJwtTokenProvider(jwt.getSecret(), jwt.getExpire());
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiJwtAuthenticationFilter lingxiJwtAuthenticationFilter(
      LingxiJwtTokenProvider provider, LingxiProperties properties) {
    LingxiProperties.Auth.Jwt jwt = properties.getAuth().getJwt();
    return new LingxiJwtAuthenticationFilter(provider, jwt.getHeader(), jwt.getPrefix());
  }

  @Bean
  @ConditionalOnMissingBean
  public LingxiAuthInterceptor lingxiAuthInterceptor() {
    return new LingxiAuthInterceptor();
  }

  @Bean
  @ConditionalOnMissingBean(LingxiLoginApi.class)
  @ConditionalOnBean(LingxiAuthenticationService.class)
  @ConditionalOnProperty(
      prefix = "lingxi.auth",
      name = "enabled",
      havingValue = "true",
      matchIfMissing = true)
  public LingxiLoginApi lingxiLoginApi(
      LingxiAuthenticationService authenticationService,
      LingxiJwtTokenProvider provider,
      LingxiProperties properties) {
    return new DefaultLingxiLoginService(
        authenticationService, provider, properties.getAuth().getJwt().getExpire());
  }

  @Bean
  @ConditionalOnMissingBean(SecurityFilterChain.class)
  public SecurityFilterChain lingxiSecurityFilterChain(
      HttpSecurity http, LingxiJwtAuthenticationFilter filter) throws Exception {
    // URL rules remain permissive because authorization is driven by @LingxiApi and
    // @LingxiAuthorize in LingxiAuthInterceptor. Applications may replace this entire chain.
    http.csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .anyRequest()
        .permitAll()
        .and()
        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
