# Lingxi（灵犀）

[English](README_EN.md) | 简体中文

> 项目状态：早期开发阶段，公开 API 在 `1.0.0` 前仍可能调整。

Lingxi 是面向 Java 8 与 Spring Boot 2.7 的接口优先 API Starter。业务团队只需在 Java 接口方法上声明 `@LingxiApi`，Lingxi 会将接口与其 Spring Bean 实现注册为标准 Spring MVC 路由。实现类无需使用 `@Controller`、`@RestController` 或 `@RequestMapping`，从而保持业务实现对传输层细节的隔离。

## 主要作用

- 用接口集中定义路径、HTTP 方法、交易码、认证要求和响应策略。
- 使用 Spring MVC 原生机制注册路由，不替换 DispatcherServlet 或 Web 容器。
- 用统一上下文承载 trace、全局流水、渠道、机构、柜员、签名及幂等号。
- 通过拦截器链扩展鉴权、审计、幂等、限流等横切能力。
- 统一正常响应、业务错误码、HTTP 状态码和未知异常脱敏。
- 通过启动清单和诊断端点排查路由与拦截器装配问题。

## 使用场景

适合：

- 采用接口与实现分离模式的 Spring MVC 单体或微服务。
- 银行、支付、政企等需要交易码、多级流水号和统一上下文的系统。
- 多团队共享认证、幂等、响应模型和异常规范的平台。
- 希望保留 Spring MVC 原生参数绑定，同时减少重复 Controller 的项目。
- 不希望引入独立 RPC 协议的契约驱动 API。

暂不适合：

- Spring Boot 3.x / Jakarta Servlet 项目，当前版本仍使用 `javax.*`。
- WebFlux 响应式应用；当前路由基础是 Spring MVC。
- 只需要少量简单 Controller、无需统一治理的小型应用。
- 需要跨语言 IDL 或二进制 RPC 的场景。

## 功能

- 接口方法驱动的 Spring MVC 动态路由
- Path、Query、Header、Cookie、Body、Multipart 和 `@LingxiParam` 参数绑定
- 统一响应包装、错误码与 HTTP 状态码映射
- 有序拦截器链和可替换的流水号、响应包装、交易码校验 SPI
- 支持按 `BEFORE_SERVICE`、`AFTER_SERVICE`、`ON_EXCEPTION`、`AFTER_COMPLETION` 自定义拦截阶段
- CompletableFuture、Callable、DeferredResult、WebAsyncTask 和 SSE
- 可选 JWT 登录、认证过滤器及角色/权限控制
- 可选 Redis 幂等能力
- 启动诊断日志和可选诊断 HTTP 端点

## 环境要求

| 组件 | 版本 |
| --- | --- |
| 运行时 | Java 8+ |
| 构建 JDK | 17（推荐） |
| Spring Boot | 2.7.x |
| Spring Framework | 5.3.x，由 Spring Boot 管理 |
| Maven | 3.8+ |

## 安装

项目尚未发布到 Maven Central。克隆仓库后执行 `mvn clean install`，再引入：

```xml
<dependency>
    <groupId>io.github.lingxi</groupId>
    <artifactId>lingxi-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## 快速开始

注解只声明在接口方法上：

```java
public interface UserApi {
    @LingxiApi(path = "/users/{id}", method = RequestMethod.GET,
        tradeCode = "USER_QUERY", auth = false)
    UserView get(@PathVariable("id") Long id);
}
```

实现类只是普通 Spring Bean：

```java
@Service
public class UserService implements UserApi {
    public UserView get(Long id) {
        return new UserView(id, "User-" + id, null);
    }
}
```

最低配置：

```yaml
lingxi:
  enabled: true
  base-package: com.example.application
  prefix: /api
  enforce-api-only: true
```

完整配置见 [配置参考](docs/configuration.md)，设计原理见 [架构设计](docs/architecture.md)。

## 示例与验证

```bash
mvn verify
mvn -pl lingxi-sample -am spring-boot:run
curl -H 'X-Trace-Id: demo-trace' http://localhost:8081/api/v1/users/42
curl http://localhost:8081/lingxi/diagnostics
```

更多请求示例和覆盖矩阵见 [测试与验收](docs/testing.md)。

## 模块

| 模块 | 职责 |
| --- | --- |
| `lingxi-core` | 注解、上下文、模型、错误码、拦截器、模板和 SPI |
| `lingxi-web` | 动态路由、参数解析、响应包装和异常处理 |
| `lingxi-security` | JWT、认证过滤器、登录契约和 RBAC |
| `lingxi-middleware` | Redis 幂等及中间件扩展点 |
| `lingxi-spring-boot-starter` | 自动配置及一站式依赖入口 |
| `lingxi-bom` | Lingxi 模块版本管理 |
| `lingxi-sample` | 可运行示例和端到端测试 |

## 生产注意事项

- 必须通过 `lingxi.auth.jwt.secret` 替换默认 JWT 密钥。
- 诊断端点默认关闭；开放时应通过网关或安全策略限制访问。
- `1.0.0` 前不默认承诺 API 向后兼容。
- 上生产前应完成组织内部的安全、容量和依赖合规评估。
- 开启 `lingxi.enforce-api-only` 后，业务包中的原生 Spring MVC Controller 或映射注解会在启动时被拒绝。

## 参与贡献

请阅读 [贡献指南](CONTRIBUTING.md)、[安全策略](SECURITY.md) 和 [行为准则](CODE_OF_CONDUCT.md)。版本变化见 [CHANGELOG](CHANGELOG.md)。

## 许可证

Copyright 2026 Lingxi Contributors。

本项目采用 [GNU Affero General Public License v3.0 only](LICENSE)（SPDX：`AGPL-3.0-only`）。如果修改本项目并通过网络向用户提供服务，AGPLv3 通常要求向这些网络用户提供正在运行版本的对应源代码。具体义务以许可证原文为准。
