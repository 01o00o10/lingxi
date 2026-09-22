// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SecurityFilterChain.class)
@ConditionalOnProperty(prefix = "lingxi.auth", name = "enabled", havingValue = "false")
public class LingxiSecurityDisabledAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean(SecurityFilterChain.class)
  public SecurityFilterChain lingxiPermitAllSecurityFilterChain(HttpSecurity http)
      throws Exception {
    http.csrf().disable().authorizeRequests().anyRequest().permitAll();
    return http.build();
  }
}
