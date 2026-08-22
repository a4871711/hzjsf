# 删除跨店结算规则实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 删除跨店结算规则的前端入口、后端接口链路、旧 SQL 种子，并通过 Flyway 清理已存在的菜单权限和规则表。

**Architecture:** 保留私教运营的其他菜单和私教收入报表计算，仅移除独立的跨店结算配置模块。运行时采用“前端入口删除 + 后端接口链路删除 + Flyway 数据清理”的三层收敛方式，避免留下动态菜单、权限、Mapper 或旧表。

**Tech Stack:** Vue 2、Element UI、Webpack 3、Node 16、Java 8、Spring MVC 4、MyBatis、MySQL、Flyway 5.2.4。

**Spec:** `docs/superpowers/specs/2026-08-18-remove-cross-store-settlement-design.md`

## Global Constraints

- 不直接连接数据库或执行 SQL；数据库清理由 `V20260818_01__remove_cross_store_settlement.sql` 在应用启动时由 Flyway 执行。
- 不修改私教收入报表的计算 SQL、Controller、Service 和 API 行为。
- 不修改预约的上课门店、订单的购买门店等其他业务字段。
- 不修改历史需求文档和历史测试报告。
- 保留工作区现有未跟踪的 `logs/`，不纳入任何提交。
- 所有新增关键注释使用简体中文；提交信息使用中文。

---

### Task 1: 先建立删除范围的失败断言

**Files:**
- Test: 临时 Node 静态断言，不写入仓库文件。

**Interfaces:**
- Consumes: 当前仓库中的跨店结算运行时引用。
- Produces: 修改前应失败、修改后可复用的残余引用检查。

- [ ] **Step 1: 运行删除前静态断言**

```bash
if rg -n -i -S \
  --glob '!admin/node_modules/**' \
  --glob '!admin/dist/**' \
  --glob '!Backend/target/**' \
  'crossStoreSettle|SysCrossStoreSettle|PtCrossStoreSettlementRule|pt_cross_store_settlement_rule|sys:crossStoreSettle' \
  admin/src Backend/src Backend/sql; then
  echo 'FAIL: 跨店结算运行时引用仍存在，符合修改前红灯预期'
  exit 1
else
  echo 'PASS: 未发现跨店结算运行时引用'
fi
```

Expected: 命令以退出码 `1` 结束，并打印当前页面、API、Controller、Service、DAO、Entity、Mapper 或旧 SQL 的命中内容。

### Task 2: 新增 Flyway 数据清理迁移并移除旧 SQL 种子

**Files:**
- Create: `Backend/src/main/resources/db/migration/V20260818_01__remove_cross_store_settlement.sql`
- Modify: `Backend/sql/sys_menu_ops.sql:41-47`
- Modify: `Backend/sql/pt_ops_domain.sql:99-115`

**Interfaces:**
- Consumes: `sys_menu.url = 'modules/sys/crossStoreSettle.html'` 和 `pt_cross_store_settlement_rule`。
- Produces: Flyway 启动迁移；旧手工脚本不再创建跨店菜单和规则表。

- [ ] **Step 1: 从旧菜单脚本删除跨店结算菜单插入块**

删除 `Backend/sql/sys_menu_ops.sql` 中的第 5 段，只保留评价、续费预警、团课转私教、异常预警和私教收入报表菜单。

- [ ] **Step 2: 从旧运营域建表脚本删除规则表 DDL**

删除 `Backend/sql/pt_ops_domain.sql` 中完整的 `CREATE TABLE pt_cross_store_settlement_rule` 块，不改相邻的续费预警、异常预警和团课转私教表。

- [ ] **Step 3: 新增幂等 Flyway 迁移**

创建文件并写入：

```sql
-- 删除跨店结算菜单的角色授权，避免 sys_role_menu 残留无效 menu_id。
DELETE FROM sys_role_menu
WHERE menu_id IN (
    SELECT menu_id
    FROM sys_menu
    WHERE url = 'modules/sys/crossStoreSettle.html'
);

-- 删除跨店结算菜单本身；父级“私教运营”及其他子菜单保留。
DELETE FROM sys_menu
WHERE url = 'modules/sys/crossStoreSettle.html';

-- 删除已废弃的全局跨店结算规则表。
DROP TABLE IF EXISTS pt_cross_store_settlement_rule;
```

迁移文件只能新增到仓库，不能在本地执行；`DROP TABLE IF EXISTS` 保证表已不存在时迁移仍可继续。

- [ ] **Step 4: 检查迁移命名和 SQL 内容**

```bash
test -f Backend/src/main/resources/db/migration/V20260818_01__remove_cross_store_settlement.sql
rg -n "sys_role_menu|modules/sys/crossStoreSettle.html|DROP TABLE IF EXISTS pt_cross_store_settlement_rule" \
  Backend/src/main/resources/db/migration/V20260818_01__remove_cross_store_settlement.sql
git diff --check
```

Expected: 文件存在，三类清理语句都能命中，差异检查退出码为 `0`。

### Task 3: 清理 admin 页面和 API

**Files:**
- Delete: `admin/src/views/modules/sys/crossStoreSettle.vue`
- Modify: `admin/src/utils/apis.js:2101-2117`

**Interfaces:**
- Consumes: 动态菜单原先加载的 `crossStoreSettle.vue` 和页面使用的两个 API 方法。
- Produces: admin 不再注册或请求 `/sys/crossStoreSettle/*`。

- [ ] **Step 1: 删除页面文件**

删除 `admin/src/views/modules/sys/crossStoreSettle.vue`，不改动态路由注册器和其他私教运营页面。

- [ ] **Step 2: 删除前端 API 方法**

从 `admin/src/utils/apis.js` 删除连续的：

```javascript
crossStoreSettle_info(data) { ... }
crossStoreSettle_save(data) { ... }
```

不得删除相邻其他 API 方法或修改 `$http` 公共封装。

- [ ] **Step 3: 检查前端残余引用**

```bash
if rg -n -i -S 'crossStoreSettle|跨店结算规则|sys:crossStoreSettle' admin/src; then
  echo 'FAIL: admin 仍有跨店结算引用'
  exit 1
else
  echo 'PASS: admin 已无跨店结算运行时引用'
fi
```

Expected: 命令退出码为 `0`。

### Task 4: 清理 Backend Controller、Service、DAO、Entity 和 Mapper

**Files:**
- Delete: `Backend/src/main/java/com/dlc/modules/sys/controller/SysCrossStoreSettleController.java`
- Delete: `Backend/src/main/java/com/dlc/modules/sys/service/SysCrossStoreSettleService.java`
- Delete: `Backend/src/main/java/com/dlc/modules/sys/service/impl/SysCrossStoreSettleServiceImpl.java`
- Delete: `Backend/src/main/java/com/dlc/modules/sys/dao/PtCrossStoreSettlementRuleDao.java`
- Delete: `Backend/src/main/java/com/dlc/modules/sys/entity/PtCrossStoreSettlementRuleEntity.java`
- Delete: `Backend/src/main/resources/mapper/sys/PtCrossStoreSettlementRuleDao.xml`
- Modify: `Backend/src/main/java/com/dlc/modules/sys/controller/SysPrivateReportController.java`
- Modify: `Backend/src/main/resources/mapper/sys/PtPrivateReportDao.xml`

**Interfaces:**
- Consumes: `/sys/crossStoreSettle/info`、`/sys/crossStoreSettle/save` 及其 `sys:crossStoreSettle:*` 权限。
- Produces: Backend 不再暴露跨店结算接口，不再注册对应 Service、DAO 或 MyBatis Mapper。

- [ ] **Step 1: 先执行 Backend 删除前引用检查**

```bash
rg -n -i -S \
  'crossStoreSettle|SysCrossStoreSettle|PtCrossStoreSettlementRule|pt_cross_store_settlement_rule|sys:crossStoreSettle' \
  Backend/src/main/java Backend/src/main/resources
```

Expected: 命中目标 Controller、Service、DAO、Entity、Mapper 以及报表过期注释。

- [ ] **Step 2: 删除六个独立功能文件**

删除本任务列出的 Controller、Service 接口、Service 实现、DAO、Entity 和 Mapper XML。删除前确认 `rg` 未发现其他业务文件调用这些类型；报表只保留自身查询实现。

- [ ] **Step 3: 清理报表过期注释**

只删除 `SysPrivateReportController.java` 和 `PtPrivateReportDao.xml` 中说明“跨店结算不落账/不拆分”的注释，不改任何查询、字段、JOIN、聚合或计算表达式。

- [ ] **Step 4: 编译 Backend**

```bash
mvn -DskipTests package
```

Run from: `Backend/`

Expected: Maven 编译和打包退出码为 `0`。项目现有测试默认跳过，不将跳过测试表述为测试通过。

### Task 5: 完成全仓运行时扫描和前端构建

**Files:**
- Verify: `admin/src/utils/apis.js`
- Verify: `Backend/src/main/java/`
- Verify: `Backend/src/main/resources/`
- Verify: `Backend/sql/`
- Verify: `Backend/src/main/resources/db/migration/V20260818_01__remove_cross_store_settlement.sql`

**Interfaces:**
- Consumes: Task 2-4 的删除结果。
- Produces: 无残余运行时入口，Flyway 文件保留为唯一数据库清理入口。

- [ ] **Step 1: 执行删除后的绿灯扫描**

```bash
if rg -n -i -S \
  --glob '!Backend/src/main/resources/db/migration/V20260818_01__remove_cross_store_settlement.sql' \
  --glob '!Backend/需求文档/**' \
  --glob '!Backend/target/**' \
  --glob '!admin/node_modules/**' \
  --glob '!admin/dist/**' \
  'crossStoreSettle|SysCrossStoreSettle|PtCrossStoreSettlementRule|pt_cross_store_settlement_rule|sys:crossStoreSettle' \
  admin/src Backend/src Backend/sql; then
  echo 'FAIL: 运行时仍有跨店结算残余引用'
  exit 1
else
  echo 'PASS: 运行时已无跨店结算残余引用'
fi
```

Expected: 命令退出码为 `0`；Flyway 迁移文件中的清理 SQL 和历史需求文档不作为运行时残余。

- [ ] **Step 2: 使用 Node 16 构建 admin**

```bash
nvm use
npm run build
```

Run from: `admin/`

Expected: 构建退出码为 `0`。项目原有 Sass/Node 弃用警告单独记录，不修改依赖。

- [ ] **Step 3: 检查工作区差异**

```bash
git diff --check
git status --short
git diff --stat
```

Expected: 只出现本计划列出的业务文件、Flyway 文件和（若采用 inline 执行）计划文件；`logs/` 保持未跟踪且不进入暂存区。

### Task 6: 提交删除实现

**Files:**
- Stage only the files listed in Tasks 2-5.
- Do not stage: `logs/`.

**Interfaces:**
- Consumes: 所有验证命令退出码为 `0` 的删除结果。
- Produces: 一个中文提交，包含跨店结算代码删除和 Flyway 迁移。

- [ ] **Step 1: 查看待提交文件和完整差异**

```bash
git status --short
git diff --stat
git diff -- Backend admin
```

- [ ] **Step 2: 暂存精确文件并检查暂存区**

```bash
git add \
  Backend/sql/sys_menu_ops.sql \
  Backend/sql/pt_ops_domain.sql \
  Backend/src/main/java/com/dlc/modules/sys/controller/SysPrivateReportController.java \
  Backend/src/main/resources/mapper/sys/PtPrivateReportDao.xml \
  Backend/src/main/resources/db/migration/V20260818_01__remove_cross_store_settlement.sql \
  admin/src/utils/apis.js \
  admin/src/views/modules/sys/crossStoreSettle.vue \
  Backend/src/main/java/com/dlc/modules/sys/controller/SysCrossStoreSettleController.java \
  Backend/src/main/java/com/dlc/modules/sys/service/SysCrossStoreSettleService.java \
  Backend/src/main/java/com/dlc/modules/sys/service/impl/SysCrossStoreSettleServiceImpl.java \
  Backend/src/main/java/com/dlc/modules/sys/dao/PtCrossStoreSettlementRuleDao.java \
  Backend/src/main/java/com/dlc/modules/sys/entity/PtCrossStoreSettlementRuleEntity.java \
  Backend/src/main/resources/mapper/sys/PtCrossStoreSettlementRuleDao.xml
git diff --cached --check
git diff --cached --stat
```

- [ ] **Step 3: 提交**

```bash
git commit -m "删除跨店结算功能"
```

- [ ] **Step 4: 提交后核验**

```bash
git show --stat --oneline --summary HEAD
git status --short
```

Expected: HEAD 为中文提交“删除跨店结算功能”，`logs/` 仍未跟踪，且没有其他用户修改被提交。
