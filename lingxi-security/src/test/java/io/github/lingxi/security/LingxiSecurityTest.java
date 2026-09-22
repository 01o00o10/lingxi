// SPDX-FileCopyrightText: 2026 Lingxi Contributors
// SPDX-License-Identifier: AGPL-3.0-only

package io.github.lingxi.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.lingxi.core.annotation.LingxiApi;
import io.github.lingxi.core.context.LingxiTradeContext;
import io.github.lingxi.core.exception.LingxiException;
import io.github.lingxi.security.annotation.LingxiAuthorize;
import io.github.lingxi.security.auth.DefaultLingxiLoginService;
import io.github.lingxi.security.auth.LingxiAuthenticationService;
import io.github.lingxi.security.auth.LingxiLoginApi;
import io.github.lingxi.security.interceptor.LingxiAuthInterceptor;
import io.github.lingxi.security.jwt.LingxiJwtPrincipal;
import io.github.lingxi.security.jwt.LingxiJwtTokenProvider;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;

class LingxiSecurityTest {
  private static final String SECRET = "01234567890123456789012345678901";

  @AfterEach
  void clear() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void createsAndParsesJwt() {
    LingxiJwtTokenProvider provider = new LingxiJwtTokenProvider(SECRET, Duration.ofMinutes(5));
    String token =
        provider.createToken(
            "user-7", Arrays.asList("ROLE_ADMIN", "user:read"), new HashMap<String, Object>());
    LingxiJwtPrincipal principal = provider.parse(token);
    assertThat(principal.getName()).isEqualTo("user-7");
    assertThat(principal.getAuthorities()).containsExactly("ROLE_ADMIN", "user:read");
  }

  @Test
  void enforcesAuthenticationAndRbacFromInterfaceMethod() throws Exception {
    SecuredService service = new SecuredService();
    Method method = SecuredService.class.getMethod("admin");
    HandlerMethod handler = new HandlerMethod(service, method);
    LingxiAuthInterceptor interceptor = new LingxiAuthInterceptor();
    assertThatThrownBy(() -> interceptor.preHandle(new LingxiTradeContext(), handler))
        .isInstanceOf(LingxiException.class);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "user-7",
                "n/a",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    LingxiTradeContext context = new LingxiTradeContext();
    assertThat(interceptor.preHandle(context, handler)).isTrue();
    assertThat(context.getUserId()).isEqualTo("user-7");
  }

  @Test
  void rejectsMissingAuthoritiesAndBypassesPublicMethods() throws Exception {
    LingxiAuthInterceptor interceptor = new LingxiAuthInterceptor();
    HandlerMethod publicHandler =
        new HandlerMethod(new SecuredService(), SecuredService.class.getMethod("publicQuery"));
    assertThat(interceptor.preHandle(new LingxiTradeContext(), publicHandler)).isTrue();

    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "user-7",
                "n/a",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    HandlerMethod adminHandler =
        new HandlerMethod(new SecuredService(), SecuredService.class.getMethod("admin"));
    assertThatThrownBy(() -> interceptor.preHandle(new LingxiTradeContext(), adminHandler))
        .isInstanceOf(LingxiException.class)
        .hasMessage("Access denied");
  }

  @Test
  void validatesJwtSecretsAndSupportsExplicitBase64Secrets() {
    assertThatThrownBy(() -> new LingxiJwtTokenProvider("too-short", Duration.ofMinutes(1)))
        .isInstanceOf(IllegalArgumentException.class);
    LingxiJwtTokenProvider provider =
        new LingxiJwtTokenProvider(
            "base64:MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=", Duration.ofMinutes(1));
    String token =
        provider.createToken("base64-user", Collections.emptyList(), Collections.emptyMap());
    assertThat(provider.parse(token).getName()).isEqualTo("base64-user");
    assertThatThrownBy(() -> provider.parse(token + "invalid"))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void authenticatesCredentialsAndReturnsAJwtLoginResponse() {
    LingxiAuthenticationService authentication =
        (username, password) ->
            new LingxiAuthenticationService.AuthenticatedUser(
                "user-9", Collections.singletonList("ROLE_USER"), Collections.emptyMap());
    LingxiJwtTokenProvider provider = new LingxiJwtTokenProvider(SECRET, Duration.ofMinutes(5));
    DefaultLingxiLoginService service =
        new DefaultLingxiLoginService(authentication, provider, Duration.ofMinutes(5));
    LingxiLoginApi.LoginRequest request = new LingxiLoginApi.LoginRequest();
    request.setUsername("demo");
    request.setPassword("secret");

    LingxiLoginApi.LoginResponse response = service.login(request);

    assertThat(response.getTokenType()).isEqualTo("Bearer");
    assertThat(response.getExpiresIn()).isEqualTo(300);
    assertThat(provider.parse(response.getToken()).getName()).isEqualTo("user-9");
  }

  interface SecuredApi {
    @LingxiApi(path = "/admin", tradeCode = "ADMIN_QUERY")
    @LingxiAuthorize(roles = "ADMIN")
    String admin();

    @LingxiApi(path = "/public", tradeCode = "PUBLIC_QUERY", auth = false)
    String publicQuery();
  }

  static class SecuredService implements SecuredApi {
    public String admin() {
      return "ok";
    }

    public String publicQuery() {
      return "public";
    }
  }
}
