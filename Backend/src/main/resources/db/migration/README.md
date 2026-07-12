# Flyway 迁移脚本目录

启动时（Spring 容器初始化 `flyway` bean）会自动按版本号顺序执行本目录下的 SQL，
执行记录写入库中的 `flyway_schema_history` 表，已执行过的脚本不会重复执行。

> 2026-07-12：曾因生产 MySQL 账号缺 global_variables 访问权限短暂停用改手动执行；
> 该权限问题已解决，Flyway 恢复启用。

## ⚠️ 脚本必须写成幂等的（不能假设所有环境都是从零开始执行）

本目录曾在 Flyway 停用期间存在过“手动执行”的过渡阶段，个别脚本的 SQL 效果可能已经
通过手动方式在某些库（尤其是开发库）生效，但未被 `flyway_schema_history` 记录。
Flyway 恢复启用后会对着这些库重新尝试执行这些脚本——`CREATE INDEX`/`ALTER TABLE ADD COLUMN`
等语句如果不做存在性判断，会因为对象已存在而报错，导致 `migrate()` 抛异常、
Spring 容器初始化失败（`sqlSessionFactory` 依赖 `flyway` 先执行）。

**新脚本涉及 DDL 时，一律先判断再执行**（`information_schema` 查询 + `PREPARE`/`EXECUTE` 动态 SQL，
或 MySQL 8.0.29+ 可直接用 `ADD COLUMN IF NOT EXISTS`/`CREATE INDEX IF NOT EXISTS`）。

## 命名规则（必须遵守，否则 Flyway 不识别）

```
V<版本号>__<描述>.sql        （V 大写，双下划线）
例：V20260710_01__vip_benefit_transfer_index.sql
```

- 版本号推荐用日期 + 当日序号（如 `20260710_01`），保证递增、避免多人冲突
- 脚本一旦被 Flyway **执行过**（即出现在某个环境的 `flyway_schema_history` 里）就**不要再改内容**
  （Flyway 校验 checksum，改了会在该环境启动报错）；要改就再加一个新版本脚本。
  尚未被任何环境的 Flyway 执行过的脚本（`flyway_schema_history` 里查不到）可以放心修改。
- 首次接入时已有库会被 baseline 记为版本 1，因此新脚本版本号必须大于 1（日期式天然满足）

## 与旧 sql/ 目录的关系

`Backend/sql/` 下是接入 Flyway **之前**的手工增量脚本，视为已随 baseline 存在于库中，
不迁入本目录。此后的新增量一律写到本目录，不再往 `sql/` 加。
