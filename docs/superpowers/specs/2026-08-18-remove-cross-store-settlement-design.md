# 删除跨店结算规则设计

日期：2026-08-18

## 1. 目标

删除“跨店结算规则”整条功能链路，包含后台页面、前端请求、后端接口、业务层、数据访问层、菜单种子和旧建表脚本。

数据库清理由 Flyway 迁移负责。开发过程不直接连接数据库、不手工执行 SQL；应用按现有 Flyway 配置启动时自动执行迁移。

## 2. 当前实现边界

扫描确认当前运行链路如下：

| 层级 | 文件 | 责任 |
| --- | --- | --- |
| 后台页面 | `admin/src/views/modules/sys/crossStoreSettle.vue` | 配置规则名称、跨店开关、收入归属方式、比例和状态 |
| 前端 API | `admin/src/utils/apis.js` | 请求 `/sys/crossStoreSettle/info` 和 `/sys/crossStoreSettle/save` |
| Controller | `Backend/src/main/java/com/dlc/modules/sys/controller/SysCrossStoreSettleController.java` | 暴露查询和保存接口 |
| Service | `Backend/src/main/java/com/dlc/modules/sys/service/SysCrossStoreSettleService.java`、`SysCrossStoreSettleServiceImpl.java` | 默认值、比例校验和 upsert |
| DAO/Entity | `PtCrossStoreSettlementRuleDao.java`、`PtCrossStoreSettlementRuleEntity.java` | 规则实体和当前规则查询 |
| Mapper | `Backend/src/main/resources/mapper/sys/PtCrossStoreSettlementRuleDao.xml` | 查询、插入和更新 `pt_cross_store_settlement_rule` |
| 菜单种子 | `Backend/sql/sys_menu_ops.sql` | 创建“跨店结算规则”菜单和两个权限 |
| 建表种子 | `Backend/sql/pt_ops_domain.sql` | 创建 `pt_cross_store_settlement_rule` |

私教收入报表没有读取该表或调用该接口，只有注释说明报表不按跨店规则拆分。删除功能后只清理这两处过期注释，不改变报表 SQL。

## 3. 删除方案

### 3.1 前端

- 删除 `admin/src/views/modules/sys/crossStoreSettle.vue`。
- 从 `admin/src/utils/apis.js` 删除 `crossStoreSettle_info` 和 `crossStoreSettle_save` 两个 API 方法。
- 不修改动态路由框架和其他“私教运营”菜单。

### 3.2 后端

删除以下没有其他业务引用的完整链路文件：

- `SysCrossStoreSettleController.java`
- `SysCrossStoreSettleService.java`
- `SysCrossStoreSettleServiceImpl.java`
- `PtCrossStoreSettlementRuleDao.java`
- `PtCrossStoreSettlementRuleEntity.java`
- `PtCrossStoreSettlementRuleDao.xml`

同步删除以下过期说明：

- `Backend/src/main/java/com/dlc/modules/sys/controller/SysPrivateReportController.java` 中关于跨店结算不落账的注释。
- `Backend/src/main/resources/mapper/sys/PtPrivateReportDao.xml` 中关于跨店结算不拆分的注释。

报表 Controller、Service、DAO、Mapper 的实际查询和计算逻辑不改。

### 3.3 旧 SQL 种子

- 从 `Backend/sql/sys_menu_ops.sql` 删除跨店菜单插入块。
- 从 `Backend/sql/pt_ops_domain.sql` 删除 `pt_cross_store_settlement_rule` 建表块。

这两个文件属于 Flyway 接入前的手工脚本。删除对应块后，新环境不会再通过旧脚本创建该功能。

### 3.4 Flyway 数据清理

新增：

`Backend/src/main/resources/db/migration/V20260818_01__remove_cross_store_settlement.sql`

迁移内容采用幂等写法：

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

Codex 不执行上述 SQL。服务启动时由现有 `flyway` Bean 按版本号自动执行，并写入 `flyway_schema_history`。

## 4. 数据影响和回滚边界

- `sys_menu` 中 URL 为 `modules/sys/crossStoreSettle.html` 的菜单记录会被删除。
- `sys_role_menu` 中指向该菜单的角色授权会被删除。
- `pt_cross_store_settlement_rule` 表及其历史规则数据会被删除。
- 该迁移没有自动 down 脚本。代码可以通过 Git 恢复，但已删除的数据库数据不能靠 Git 恢复。
- 不删除“私教运营”父菜单，不影响评价、续费预警、团课转私教、异常预警和收入报表。

## 5. 验证方案

1. 修改前用静态断言确认目标页面、API、Controller、Service、DAO、Entity、Mapper 和 SQL 引用存在。
2. 修改后用 `rg` 确认运行代码中不再存在 `crossStoreSettle`、`SysCrossStoreSettle`、`PtCrossStoreSettlementRule` 和对应表名。
3. 检查 Flyway 文件名、版本号和幂等清理语句。
4. 执行 `git diff --check`。
5. 在 `Backend` 使用项目既有 Maven 命令编译，验证删除 Java/Mapper 后没有残余引用。
6. 在 `admin` 使用 Node 16 执行 `npm run build`，验证删除页面和 API 后前端构建正常。
7. 不连接数据库、不执行 Flyway、不启动服务；数据库迁移效果由用户后续启动本地环境时验证。

## 6. 不在本次范围内

- 不修改私教收入报表的收入、成本、毛利计算。
- 不修改其他跨店相关的业务字段，例如预约的上课门店和订单的购买门店。
- 不改写需求文档和历史测试报告；这些文件保留为历史设计记录，不属于运行逻辑。
- 不处理与本功能无关的 `20260728_pt_commission_settlement.sql` 课时费/教练提成结算逻辑。
