# Contributing to Lingxi

感谢你愿意参与 Lingxi。提交代码前，请先搜索已有 Issue，较大的功能建议先通过 Issue 讨论接口和兼容性边界。

## 开发环境

- JDK 8 或更高版本
- Maven 3.8 或更高版本
- Redis（仅在手工验证 Redis 幂等能力时需要）

## 本地验证

```bash
mvn verify
```

该命令会执行单元测试、集成测试和 Spotless 格式检查。只验证 sample：

```bash
mvn -pl lingxi-sample -am test
```

## 提交约定

- 每个可观察行为都应有对应测试，修复缺陷时先增加回归测试。
- `@LingxiApi` 只声明在接口方法上，避免把框架实现细节带入业务实现类。
- 保持 Java 8 兼容，不使用更高版本语言特性。
- 不提交 `target/`、IDE 配置、日志、密钥或真实业务数据。
- 一个 Pull Request 聚焦一个主题，并说明行为变化、兼容性影响和验证方式。

推荐使用 `feat:`、`fix:`、`docs:`、`test:`、`refactor:`、`build:` 等清晰前缀，但不强制要求。

## Pull Request 检查项

- [ ] 已说明修改目的和使用场景
- [ ] 已补充或更新测试
- [ ] `mvn verify` 通过
- [ ] 已同步 README 或 `docs/` 中受影响的内容
- [ ] 未引入凭据、个人信息或不兼容的依赖升级

提交贡献即表示你同意按项目的 GNU Affero General Public License v3.0 only 授权该贡献。
