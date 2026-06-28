# VIP 会员权益转让 — 开发任务拆分（逐步实施清单）

> 依据：《VIP会员权益转让_详细技术设计.md》（命名/枚举/DDL/接口/事务以那份为准，尤其第 13 章附录为最终口径）。
> 用途：把实现拆成 15 个可独立交付、可单独验收的步骤，按顺序推进。每步给出「目标 / 新建文件 / 改动文件 / 关键点 / 验收」。
> 总体顺序：先地基（建表+常量）→ 再独立小项（门店等级/权益卡/购买/停卡，风险低、能单独验收）→ 再转让主流程（核心）→ 最后黑名单 + admin 前端。

---

## 进度总览

| 步 | 任务 | 里程碑 | 依赖 | 状态 |
|---|---|---|---|---|
| 1 | 数据库 DDL 脚本 | 地基 | — | ✅ 已完成 |
| 2 | 公共常量 + 统一返回码 | 地基 | 1 | ☐ |
| 3 | 门店等级 store_level 贯通 | 独立小项 | 1 | ☐ |
| 4 | 转让费用规则 vip_fee_rule 后台 CRUD | 独立小项 | 1,2 | ☐ |
| 5 | 权益卡商品 vip_benefit_card 后台 CRUD | 独立小项 | 1,2,4 | ☐ |
| 6 | 动态定价 + 权益卡 list/detail（移动端） | 独立小项 | 1,2,5 | ☐ |
| 7 | 权益卡购买 buy + 微信回调激活 + myBenefits | 独立小项 | 1,2,6 | ☐ |
| 8 | 停卡 card_pause（apply/list + 限次 + 顺延） | 独立小项 | 1,2 | ☐ |
| 9 | 转让前置校验 + 费用分档计算 | 转让主流程 | 1,2,7 | ☐ |
| 10 | 转让发起/试算/撤回/确认/拒绝 + 状态机 | 转让主流程 | 9 | ☐ |
| 11 | 转让费用回调 + 过户单事务 | 转让主流程 | 10 | ☐ |
| 12 | 退费 doRefund + 受让超时定时任务 | 转让主流程 | 11 | ☐ |
| 13 | 后台转让审核 list/audit | 转让主流程 | 11,12 | ☐ |
| 14 | 会员黑名单 + 接入校验 | 收尾 | 9 | ☐ |
| 15 | admin 前端 5 页面 + apis.js + 菜单 | 收尾 | 3,5,13,14 | ☐ |

> 文件路径约定：后端根 `Backend/src/main/java/com/dlc/`，Mapper XML 在 `Backend/src/main/resources/mapper/{api,sys}/`；前端根 `admin/src/`。**改 `mapper/api/*.xml` 需重启 Tomcat，改 `mapper/sys/*.xml` 热刷新。**

---

## 第 1 步 · 数据库 DDL 脚本 ✅

- **目标**：建好全部新表与改造，含并发护栏唯一键。
- **新建**：`Backend/sql/vip_benefit_transfer.sql`。
- **内容**：6 张新表（`vip_fee_rule`/`vip_benefit_card`/`vip_benefit`/`vip_benefit_transfer`/`card_pause_record`/`member_blacklist`）+ `ALTER store ADD store_level` + 唯一键 `uk_source_order`、`uk_fee_order`、`uk_card_month`。
- **验收**：在开发库执行脚本无报错、6 表 + `store.store_level` 就位。

## 第 2 步 · 公共常量 + 统一返回码

- **目标**：订单后缀常量与全部 VIP 错误码就位（后续各步都引用）。
- **改动**：
  - `common/utils/ConfigConstant.java`：加 `VIP_CARD_BUY_TYPE="6"`、`VIP_TRANSFER_FEE_TYPE="7"`（确认不与现有 2/3/4/5 冲突）。
  - `common/utils/CodeAndMsg.java`：按**附录 A 统一码表**一次性加 `ERROR_VIP_*`（-50 ~ -70）。
- **关键点**：码值/命名以详细设计附录 A 为唯一来源，正文早稿里的旧码值作废。
- **验收**：编译通过；常量可被引用。

## 第 3 步 · 门店等级 store_level 贯通

- **目标**：门店可维护"等级"，供转让校验"源≥目标"。
- **改动**：`sys/entity/SysStoreEntity.java`（加 `storeLevel`）、`mapper/sys/StoreDao.xml`（insert/update/select 补 `store_level`）、`sys/controller/SysStoreController.java` 与 service 透传；如移动端需读取再补 `api/entity/Store.java`。
- **验收**：后台门店新增/编辑能存取等级；列表展示等级列。

## 第 4 步 · 转让费用规则 vip_fee_rule 后台 CRUD

- **目标**：后台配置"按转让次数分档"的费用表。
- **新建**：`sys/entity/VipFeeRuleEntity.java`、`sys/dao/VipFeeRuleDao.java`、`mapper/sys/VipFeeRuleDao.xml`、`sys/service/VipFeeRuleService.java` + `impl/VipFeeRuleServiceImpl.java`、`sys/controller/SysVipFeeRuleController.java`（`sys/vipFeeRule` list/info/save/update/delete）。
- **关键点**：save/update **校验 `tiersJson`**（合法 JSON 数组、`fromCount` 正整数升序去重、`fee≥0`），不合法返回 `ERROR_VIP_FEE_RULE_FORMAT`；delete 前查是否被 `vip_benefit_card.fee_rule_id` 引用，被引用拒删。
- **验收**：后台增删改查费用规则；非法分档被拦截。

## 第 5 步 · 权益卡商品 vip_benefit_card 后台 CRUD

- **目标**：后台管理可购买的权益卡商品（含动态定价配置）。
- **新建**：`sys/entity/VipBenefitCardEntity.java`、`sys/dao/VipBenefitCardDao.java`、`mapper/sys/VipBenefitCardDao.xml`、`sys/service/VipBenefitCardService.java` + impl、`sys/controller/SysVipCardController.java`（list/info/save/update/delete/onCard/offCard）。仿 `SysFitCardController`。
- **关键点**：动态定价字段（`base_buy_count/step_num/step_add_price/price_cap`）+ `fee_rule_id` 可配；**`sold_count` 只读、不进新增/编辑表单**（后台改不了人数）。
- **验收**：后台增删改查权益卡、上下架；`sold_count` 不可手改。

## 第 6 步 · 动态定价 + 权益卡 list/detail（移动端）

- **目标**：小程序能浏览权益卡，看到实时价与购买人数。
- **新建**：`api/entity/VipBenefitCard.java`、`api/dao/VipBenefitCardMapper.java`、`mapper/api/VipBenefitCardMapper.xml`、`api/service/VipCardService.java` + impl（`calcCurrentPrice`）、`api/controller/VipCardController.java`（`GET /api/vipCard/list`、`/detail`）。
- **关键点**：`currentPrice` 后端按当前 `sold_count` 实时算（附录公式）；`soldCount` 仅 `show_buy_count=1` 时返回。
- **验收**：list/detail 返回正确 `currentPrice`，跨人数阈值价格按规则跳档。

## 第 7 步 · 权益卡购买 buy + 微信回调激活 + myBenefits

- **目标**：买权益卡→微信支付→回调激活权益、`sold_count+1`，幂等。
- **新建**：`api/entity/VipBenefit.java`、`api/dao/VipBenefitMapper.java`、`mapper/api/VipBenefitMapper.xml`、`api/service/VipBenefitService.java` + impl（`activateByOrderNo`）；`VipCardController` 加 `POST /api/vipCard/buy`、`GET /api/vipCard/myBenefits`。
- **改动**：
  - `api/service/impl/IncomePayDetailServiceImpl.java`：加后缀 `6` 分支，按 `source_order_no` 反查 `vip_benefit` 取 userId/storeId（附录 B.2）。
  - `api/controller/PayController.java`：`rechargePay` 加权益卡购买下单分支（仅微信）；`rechargeCallBack` 加后缀 `6` → `vipBenefitService.activateByOrderNo(...)` 分支。
- **关键点**：下单后端重算价→建 `vip_benefit status=9待支付`；激活 = **单 service 方法内**（记账+置 0 正常+算到期+`sold_count+1`），靠 `WHERE source_order_no=? AND status=9` 幂等（附录 B.3）。
- **验收**：走通"下单→支付→回调"，权益变正常、`sold_count+1`；重复回调不二次自增。

## 第 8 步 · 停卡 card_pause

- **目标**：会员自助停卡，受"每月 1 次/全年 12 次/有效期顺延"约束。
- **新建**：`api/entity/CardPauseRecord.java`、dao、`mapper/api/CardPauseRecordMapper.xml`、`api/service/CardPauseService.java` + impl、`api/controller/CardPauseController.java`（`POST /api/cardPause/apply`、`GET /list`）。
- **关键点**：apply `SELECT ... FOR UPDATE` 串行化 + 库层 `uk_card_month` 兜底；恢复时按**实际停卡天数**顺延 `card_order.validityDate`（附录 D.2）。
- **验收**：同卡当月第 2 次被拦；恢复后到期日顺延正确。

## 第 9 步 · 转让前置校验 + 费用分档计算

- **目标**：可复用的校验集与费用计算（发起、审核两处都用）。
- **新建**：`api/entity/VipBenefitTransfer.java`、dao、`mapper/api/VipBenefitTransferMapper.xml`、`api/service/VipTransferService.java` + `impl/VipTransferServiceImpl.java`（`checkTransferable`、`calcTransferFee`）。
- **关键点**：`checkTransferable` 覆盖 R-08/17 全部情形（有效期/封禁/黑名单/欠费占位/退卡占位/违规占位/门店等级/属本人/不转给自己/卡未下架），命中抛附录 A 对应码；`calcTransferFee` 解析 `tiers_json` 按 `transfer_count+1` 命中。**校验集放在 api 的 service.impl，sys 审核直接 `@Autowired` 注入复用（同一 Spring 容器）**（附录 E）。
- **验收**：单测各校验分支与费用命中（含边界：step=0、封顶、空规则=免费）。

## 第 10 步 · 转让发起/试算/撤回/确认/拒绝 + 状态机

- **目标**：移动端转让全流程接口（除回调/过户）。
- **改动/扩展**：`api/controller/VipTransferController.java`（`quote/apply/myList/withdraw/confirm/reject`）、`VipTransferServiceImpl`。
- **关键点**：`apply` 先 `SELECT ... FOR UPDATE where vip_benefit_id and status in (10,20,40)` **在途唯一占用**，存在抛 `ERROR_VIP_TRANSFER_IN_PROGRESS`（附录 C.1）；费用=0 直接到 `20待审核`，>0 建 `10待付费`+下单；撤回 `20→退费`、`40→不退`（附录 D.1）。
- **验收**：状态流转正确；同权益不能并发两笔在途。

## 第 11 步 · 转让费用回调 + 过户单事务

- **目标**：费用支付到账→待审核；受让人确认→过户生效。
- **扩展**：`VipTransferServiceImpl`（`payFeeCallback`、`transferEffect`）。
- **改动**：`IncomePayDetailServiceImpl` 加后缀 `7` 分支（反查 `vip_benefit_transfer` 取 from 侧 userId/storeId）；`PayController.rechargeCallBack` 加后缀 `7` 分支。
- **关键点**：`payFeeCallback` `WHERE service_fee_order_no=? AND status=10 → 20`；`transferEffect` **单事务**：`SELECT ... FOR UPDATE` 权益→改归属 user_id/`transfer_count+1`/`transferable=1`/**`expire_time` 不变（继承）**→`status=40→70` 幂等→`update income_pay_detail set another_id`→推送双方（附录 B.3/C.3）。
- **验收**：确认后原持有人失效、受让人获权益且到期日不变；重复确认不重复过户。

## 第 12 步 · 退费 doRefund + 受让超时定时任务

- **目标**：驳回/拒绝/超时/20 撤回自动退费；超时自动关单。
- **扩展**：`VipTransferServiceImpl.doRefund`（`wxRefund` + `refund_status=1` + 负向 `income_pay_detail` 用途 8）。
- **新建**：`api/schedule/VipTransferTimeoutTask.java`（`scanConfirmTimeout`：`status=40 且 confirm_deadline<now → 52 + 退费 + 推送`）。
- **改动**：`spring-mvc.xml` 注册该定时任务。
- **验收**：各退费分支真退款、账面有负向流水；超时单自动转 52 并退费。

## 第 13 步 · 后台转让审核 list/audit

- **目标**：门店后台审核工作台。
- **新建**：`sys/controller/SysVipTransferController.java`（`/sys/vipTransfer/list`、`/audit`）+ sys 侧查询 dao/service。
- **关键点**：`audit` 注入 api 的 `checkTransferable` 复核；通过→`40`+`confirm_deadline`+推送受让人，驳回→`31`+`doRefund`+推送；复核失败统一走 31+退费、`audit_remark` 标"系统复核"。
- **验收**：列表按状态筛选；通过/驳回流转与退费/推送正确。

## 第 14 步 · 会员黑名单 + 接入校验

- **目标**：后台拉黑会员，禁止其发起/接收转让。
- **新建**：`sys/entity/MemberBlacklistEntity.java` + dao/mapper/service/`SysMemberBlacklistController.java`（list/save 拉黑/remove 解除）；`api` 侧黑名单查询 dao（供校验用）。
- **改动**：`checkTransferable` 接入黑名单实时查（配合 `auditStatus=2`）。
- **验收**：拉黑后该会员发起/被指定为受让人时被拦。

## 第 15 步 · admin 前端 5 页面 + apis.js + 菜单

- **目标**：后台可视化操作页（对应《Web后台界面设计文档》）。
- **新建**：`admin/src/views/modules/sys/{vipCard,vipFeeRule,vipTransfer,cardPause,memberBlacklist}.vue`（配置式 r-search/r-table/r-form）。
- **改动**：`admin/src/utils/apis.js` 新增对应接口方法；后端 `sys_menu` 新增菜单 + 按钮权限记录（菜单 url=`sys/vipCard` 等，映射到上面 vue）。
- **验收**：菜单出现、各页增删改查/审核可操作并联通后端。

---

## 环境与验证说明（重要）

- **编译**：项目 `pom.xml` `java-version=1.8`，本机默认 JDK 17。编译验证须用 JDK 8：`/usr/libexec/java_home -v 1.8`（若装了 8）切换后 `cd Backend && mvn -q -DskipTests compile`。本机若无 JDK 8，Java 代码的编译验证需在你本地环境做，我会给出可执行命令与预期。
- **运行/接口验证**：需 MySQL + Redis + Tomcat（`mvn tomcat7:run`，上下文 `/renren-security`，端口 8080）。前端 `admin`：Node 16 + `npm run dev`（端口 8001，代理到后端）。
- **微信支付**：本地无法真正完成微信支付/退款（需真实商户配置 + 公网回调）。购买/转让费用/退费的**支付链路**以"下单参数正确生成 + 回调处理逻辑正确"为验收口径，真实支付走真机/测试号。
- **数据库**：第 1 步 DDL 可由我用连接的 MySQL 工具在**开发库**执行验证（需你确认非生产），或你自行执行。
- **提交**：默认只改工作区、不 commit；需要提交时走 `/commit`（会先 code-review 再提）。

---

## 落地建议

1. 先把 **1→2** 地基铺好（建表 + 常量码表），后面所有步骤都依赖它。
2. 再做 **3、4、5、6、7、8** 这批独立小项——每个都能单独编译、单独在后台/小程序验收，风险最低、最快见到东西。
3. 然后 **9→10→11→12→13** 啃转让主流程（核心、相互依赖，按序来）。
4. 最后 **14、15** 黑名单与前端页面收尾。
