# Lingxi

English | [简体中文](README.md)

> Project status: early development. Public APIs may change before `1.0.0`.

Lingxi is an interface-first API starter for Java 8 and Spring Boot 2.7. Teams declare `@LingxiApi` on Java interface methods, and Lingxi registers those contracts and their Spring Bean implementations as standard Spring MVC routes. Implementations remain free of `@Controller`, `@RestController`, and `@RequestMapping` annotations.

## Purpose

- Define paths, HTTP methods, trade codes, authentication, and response policies on interfaces.
- Register routes through native Spring MVC without replacing DispatcherServlet or the web runtime.
- Carry trace IDs, sequence numbers, channel data, signatures, and idempotency keys in one request context.
- Apply authentication, auditing, idempotency, and other cross-cutting behavior through ordered interceptors.
- Standardize responses, business errors, HTTP statuses, and unexpected-error sanitization.
- Diagnose route and interceptor registration through startup reports and an optional endpoint.

## Use Cases

Lingxi is a good fit for:

- Spring MVC monoliths or microservices that separate contracts from implementations.
- Banking, payment, government, and enterprise systems with trade codes and multi-level sequence numbers.
- Platforms that need consistent authentication, idempotency, responses, and errors across teams.
- Projects that want native Spring MVC binding with less repetitive controller code.
- Contract-driven APIs that do not require a separate RPC protocol.

Lingxi is not currently intended for:

- Spring Boot 3.x or Jakarta Servlet applications; this release uses `javax.*` APIs.
- Reactive WebFlux applications; the routing layer is based on Spring MVC.
- Small applications with no shared API governance requirements.
- Cross-language IDL or binary RPC use cases.

## Features

- Interface-method-driven Spring MVC routes
- Path, query, header, cookie, body, multipart, and `@LingxiParam` binding
- Unified response envelopes, error codes, and HTTP status mapping
- Ordered interceptors and replaceable sequence, response, and trade-code SPIs
- CompletableFuture, Callable, DeferredResult, WebAsyncTask, and SSE support
- Optional JWT login, authentication filter, and role/authority checks
- Optional Redis-backed idempotency
- Startup diagnostics and an optional diagnostics HTTP endpoint

## Requirements

| Component | Version |
| --- | --- |
| Runtime | Java 8+ |
| Build JDK | 17 recommended |
| Spring Boot | 2.7.x |
| Spring Framework | 5.3.x, managed by Spring Boot |
| Maven | 3.8+ |

## Installation

Lingxi is not on Maven Central yet. Run `mvn clean install`, then add:

```xml
<dependency>
    <groupId>io.github.lingxi</groupId>
    <artifactId>lingxi-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

Declare the API on an interface method:

```java
public interface UserApi {
    @LingxiApi(path = "/users/{id}", method = RequestMethod.GET,
        tradeCode = "USER_QUERY", auth = false)
    UserView get(@PathVariable("id") Long id);
}
```

Keep the implementation as an ordinary Spring Bean:

```java
@Service
public class UserService implements UserApi {
    public UserView get(Long id) {
        return new UserView(id, "User-" + id, null);
    }
}
```

Minimum configuration:

```yaml
lingxi:
  enabled: true
  base-package: com.example.application
  prefix: /api
```

## Sample and Verification

```bash
mvn verify
mvn -pl lingxi-sample -am spring-boot:run
curl -H 'X-Trace-Id: demo-trace' http://localhost:8081/api/v1/users/42
curl http://localhost:8081/lingxi/diagnostics
```

See the [documentation index](docs/README.md), [configuration reference](docs/configuration.md), and [test matrix](docs/testing.md).

## Modules

| Module | Responsibility |
| --- | --- |
| `lingxi-core` | Annotations, context, models, errors, interceptors, templates, and SPIs |
| `lingxi-web` | Dynamic routes, argument resolution, response wrapping, and error handling |
| `lingxi-security` | JWT, authentication filter, login contract, and RBAC |
| `lingxi-middleware` | Redis idempotency and middleware extension points |
| `lingxi-spring-boot-starter` | Auto-configuration and application dependency entry point |
| `lingxi-bom` | Lingxi module version management |
| `lingxi-sample` | Runnable examples and end-to-end tests |

## Production Notes

- Replace the default JWT secret through `lingxi.auth.jwt.secret`.
- The diagnostics endpoint is disabled by default. Restrict access if enabled.
- Backward compatibility is not guaranteed before `1.0.0`.
- Perform security, capacity, and dependency compliance reviews before production use.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md), [SECURITY.md](SECURITY.md), and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). Changes are recorded in [CHANGELOG.md](CHANGELOG.md).

## License

Copyright 2026 Lingxi Contributors.

Licensed under the [GNU Affero General Public License v3.0 only](LICENSE) (`AGPL-3.0-only`). If you modify Lingxi and provide access to it over a network, AGPLv3 generally requires offering the corresponding source of the running version to those network users. Refer to the license text for the exact terms.
