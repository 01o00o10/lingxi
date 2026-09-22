# Changelog

本项目采用 [Semantic Versioning](https://semver.org/)；重要变更记录在此文件中。

## [Unreleased]

### Added

- 基于接口方法 `@LingxiApi` 的 Spring MVC 动态路由。
- 统一上下文、响应包装、异常映射和可扩展拦截器链。
- CompletableFuture、Callable、DeferredResult、WebAsyncTask 和 SSE 支持。
- JWT 登录、认证过滤器及角色/权限校验。
- Redis 幂等拦截器。
- 启动诊断日志和可选诊断 HTTP 端点。
- 可运行 sample 与覆盖主要公开行为的自动化测试。

### Changed

- 自动配置代码合并至 `lingxi-spring-boot-starter`，减少独立发布构件。
- 项目许可证由 Apache License 2.0 调整为 GNU AGPL v3.0 only。
- README 拆分为中文和英文版本，并补充框架定位与适用场景。
