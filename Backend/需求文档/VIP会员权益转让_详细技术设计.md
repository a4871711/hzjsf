# VIP 会员权益转让 — 详细技术设计

> 上游文档：《VIP会员权益转让_需求梳理与实现方案.md》（业务规则、决策点 D-1~D-10 以那份为准）、《VIP会员权益转让_Web后台界面设计文档.md》（后台页面视觉）。
> 目标系统：hzjsf 健身房系统。仓库前后端一体：`Backend/`（Java：Spring MVC + MyBatis-Plus + Shiro，`api` 移动端 + `sys` 后台两套接口）、`admin/`（renren-fast-vue + Element UI 后台前端）。
> 本文用途：把需求落成可直接开工的技术方案——建表 DDL、接口入参出参、Service 方法与事务/幂等写法、改动文件清单、前端改动、落地顺序与自测清单。
> 范围：本功能只接**微信支付**；表结构、状态码、命名以第 2、3 节为唯一事实来源。
>
> ⚠️ **编码前必读**：第 5–11 章由多 agent 并行起草，第 **12 章（设计评审）** 对照真实代码挖出 6 个高危硬伤（记账方法签名、回调事务边界、码值冲突、并发占用等），第 **13 章（附录 A–F）已逐条拍板修正并作为最终口径**。**凡正文与第 13 章冲突，一律以第 13 章为准**——尤其：CodeAndMsg 用附录 A 统一码表；记账/回调原子性用附录 B；并发占用用附录 C；撤回/停卡/门店口径用附录 D。

---
## 1. 设计约定与决策摘要

### 1.1 技术栈与基础约定

- 框架：Spring MVC + MyBatis-Plus + Shiro；MySQL 8 / InnoDB / `utf8mb4`；根包 `com.dlc`。
- 移动端 `api`：返回 `R.reOk(data)`（code=1）/ `R.reError(CodeAndMsg)`（code=0）；身份用 `BaseController.getUserId(req)`，需校验封禁的接口用 `getUserVo(req)`（`auditStatus=2` 抛 `ERROR_USER_IS_LOCK`）。
- 后台 `sys`：返回 `R.ok()` / `R.error()`；鉴权用 `@RequiresPermissions("sys:xxx:yyy")`。
- 事务：`com.dlc.modules.api.service.impl` 与 `sys.service.impl` 下**所有方法**自动 `REQUIRED` 事务（事务切面已覆盖，无需注解）。**过户必须在单事务内完成**。
- 支付：本功能**只接微信**，不接支付宝 / 钱包等其他渠道。
- Mapper XML 生效规则：`api` 模块的 Mapper XML 改动**需重启 Tomcat** 才生效；`sys` 模块的 Mapper XML 支持热刷新。
- 订单号：`OrderNoGenerator.getOrderIdByTime()`（= `yyyyMMddHHmmss` + 9 位随机），再拼末位「订单类型后缀字符」区分用途。

### 1.2 已确认决策摘要（依据需求文档第 4 节）

| 编号 | 决策结论 |
|---|---|
| D-1 | 独立可购买的「权益卡」（商品 `vip_benefit_card` + 用户持有实例 `vip_benefit`），与会员卡（`fit_card`/`card_order`）平行、相互独立。权益卡本身即被转让对象；会员卡仍归原办卡人、负责入场、永不转让。 |
| D-3 | 转让费用**全部由转让人在发起时缴**，受让人不付费；受让人确认接收即生效，无需支付任何费用。 |
| D-4 | 转让费用**按「该权益已被转让的次数」分档定额**（后台可配的 `vip_fee_rule.tiers_json`）；第 1 次可设为免费，最后一档兜底所有更高次数。 |
| D-5 | 停卡：年卡**每自然月 1 次、全年 12 次**，有效期顺延、停卡期间不可入场、**无需后台审核**（会员自助）。 |
| D-9 | **允许链式多次转让**（A→B→C→…）；`transfer_count` 记录该权益已被转让次数，供分档费用命中。受让权益仍不支持退费。 |
| D-10 | 受让人**继承剩余有效期，到期日不变、不重置**；时长只会越转越少，堵死「链式无限续期」漏洞。 |
| D-7 | 动态涨价涨的是「**购买价**」；`sold_count` 由系统维护、后台只读。 |

### 1.3 关键计算约定

- **动态购买价**：设 `n=sold_count`。若 `step_num<=0` 或 `step_add_price<=0` 返回 `price`；若 `n<=base_buy_count` 返回 `price`；否则 `tier=floor((n-base_buy_count)/step_num)`，`p=price+tier*step_add_price`，若 `price_cap` 非空则 `p=min(p, price_cap)`。下单时后端按当前 `sold_count` **重算，不信前端**；`sold_count` 仅在购买支付成功回调 `+1`（转让不 +）。
- **转让费用分档**：本次为该权益第 `(transfer_count+1)` 次转让；在 `fee_rule.tiers`（按 `fromCount` 升序）中取 `fromCount<=本次` 的最大档 `fee`；无匹配则为 0。

---

## 2. 数据库设计

> 所有表名 / 字段 / 状态码以下方 DDL 为唯一事实来源。新增建表脚本放 `Backend/sql/`。

### 2.1 `vip_fee_rule` — VIP 转让费用规则（分档）

用途：保存后台可配的「按转让次数分档定额」费用表，`tiers_json` 存档位数组，被 `vip_benefit_card.fee_rule_id` 引用。索引：主键 `fee_rule_id`。

```sql
CREATE TABLE vip_fee_rule (
  fee_rule_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '转让费用规则ID',
  rule_name VARCHAR(64) NOT NULL COMMENT '规则名称',
  tiers_json VARCHAR(1024) NOT NULL COMMENT '分档JSON 如 [{"fromCount":1,"fee":0},{"fromCount":2,"fee":50},{"fromCount":3,"fee":100}]',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (fee_rule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP转让费用规则(分档)';
```

### 2.2 `vip_benefit_card` — VIP 权益卡商品

用途：可单独购买、可上下架的权益卡商品定义，承载售价、有效天数、适用门店、动态涨价参数与购买人数统计。`sold_count` 系统维护、后台只读；下单时按 `sold_count` 重算购买价。索引：主键 `vip_card_id`。

```sql
CREATE TABLE vip_benefit_card (
  vip_card_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '权益卡商品ID',
  card_name VARCHAR(64) NOT NULL COMMENT '权益卡名称',
  benefit_desc VARCHAR(1024) DEFAULT NULL COMMENT '权益内容描述',
  price DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '基础售价(购买价)',
  validity_days INT NOT NULL DEFAULT 365 COMMENT '有效天数',
  store_addr_ids VARCHAR(512) DEFAULT NULL COMMENT '适用门店ID(逗号分隔)',
  fee_rule_id BIGINT DEFAULT NULL COMMENT '关联转让费用规则',
  show_buy_count TINYINT NOT NULL DEFAULT 1 COMMENT '是否展示实时购买人数 1是0否',
  base_buy_count INT NOT NULL DEFAULT 0 COMMENT '起涨基数:已购<=此值不涨价',
  step_num INT NOT NULL DEFAULT 0 COMMENT '每多少人涨一档(0=不动态涨价)',
  step_add_price DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '每档加价',
  price_cap DECIMAL(10,2) DEFAULT NULL COMMENT '封顶价(NULL=不封顶)',
  sold_count INT NOT NULL DEFAULT 0 COMMENT '真实累计购买人数(系统维护,后台只读)',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1上架 2下架',
  created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (vip_card_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP权益卡商品';
```

### 2.3 `vip_benefit` — 用户持有的权益卡实例（被转让对象）

用途：用户购买后生成的权益实例，是转让的标的；记录当前持有人、原始购买人留痕、有效期、已被转让次数与可转标记。转让只改 `user_id`、`transfer_count` 等，`expire_time` 不变（继承剩余有效期）。索引：主键 `vip_benefit_id`；`idx_user(user_id)`、`idx_card(vip_card_id)`、`idx_order(source_order_no)`。

```sql
CREATE TABLE vip_benefit (
  vip_benefit_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '当前持有人',
  origin_user_id BIGINT NOT NULL COMMENT '原始购买人(留痕,永不变)',
  vip_card_id BIGINT NOT NULL COMMENT '来源权益卡商品',
  source_order_no VARCHAR(48) DEFAULT NULL COMMENT '购买订单号',
  store_id BIGINT DEFAULT NULL COMMENT '购买/归属门店',
  store_addr_id BIGINT DEFAULT NULL,
  origin_price DECIMAL(10,2) DEFAULT NULL COMMENT '购买时售价(留痕,不参与转让费用计算)',
  start_time DATETIME DEFAULT NULL COMMENT '生效',
  expire_time DATETIME DEFAULT NULL COMMENT '到期',
  status TINYINT NOT NULL DEFAULT 9 COMMENT '9待支付 0正常 1已转出失效 2已冻结 3已过期',
  transfer_count INT NOT NULL DEFAULT 0 COMMENT '已被转让次数',
  transferable TINYINT NOT NULL DEFAULT 1 COMMENT '是否可转 1是0否',
  created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (vip_benefit_id),
  KEY idx_user (user_id), KEY idx_card (vip_card_id), KEY idx_order (source_order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户持有的权益卡实例(被转让对象)';
```

### 2.4 `vip_benefit_transfer` — 权益转让申请/记录（状态机）

用途：一次转让申请的全生命周期记录，驱动「待付费→待审核→待受让人确认→生效/驳回/拒绝/超时/撤回」状态机；记录服务费、微信支付/交易号、审核留痕、确认截止时间与退费状态。索引：主键 `transfer_id`；唯一键 `uk_fee_order(service_fee_order_no)`；`idx_benefit`、`idx_from`、`idx_to`、`idx_status`。

```sql
CREATE TABLE vip_benefit_transfer (
  transfer_id BIGINT NOT NULL AUTO_INCREMENT,
  vip_benefit_id BIGINT NOT NULL,
  from_user_id BIGINT NOT NULL COMMENT '转让人',
  to_user_id BIGINT NOT NULL COMMENT '受让人',
  from_store_id BIGINT DEFAULT NULL,
  to_store_id BIGINT DEFAULT NULL,
  service_fee DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '转让费用(转让人付)',
  service_fee_order_no VARCHAR(48) DEFAULT NULL COMMENT '转让费用微信支付单号',
  transaction_number VARCHAR(64) DEFAULT NULL COMMENT '微信交易号',
  status TINYINT NOT NULL COMMENT '10待付费 20待审核 31已驳回 40待受让人确认 51已拒绝 52已超时 60已撤回 70已生效',
  audit_user_id BIGINT DEFAULT NULL,
  audit_time DATETIME DEFAULT NULL,
  audit_remark VARCHAR(512) DEFAULT NULL,
  confirm_deadline DATETIME DEFAULT NULL COMMENT '受让人确认截止时间',
  refund_status TINYINT NOT NULL DEFAULT 0 COMMENT '0未退 1已退',
  created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  effect_time DATETIME DEFAULT NULL COMMENT '生效时间',
  PRIMARY KEY (transfer_id),
  UNIQUE KEY uk_fee_order (service_fee_order_no),
  KEY idx_benefit (vip_benefit_id), KEY idx_from (from_user_id), KEY idx_to (to_user_id), KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权益转让申请/记录(状态机)';
```

### 2.5 `card_pause_record` — 停卡记录

用途：记录年卡停卡申请（被停的 `card_order`、所属自然月、停卡天数、起止与状态），用于约束「每月 1 次 / 全年 12 次」并支撑有效期顺延。`pause_month` 用于月度限次判断；`end_time` 为 NULL 表示停卡中。索引：主键 `pause_id`；`idx_user(user_id)`、`idx_card(card_order_id)`。

```sql
CREATE TABLE card_pause_record (
  pause_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  card_order_id BIGINT NOT NULL COMMENT '被停的卡(card_order)',
  pause_month CHAR(7) DEFAULT NULL COMMENT '所属自然月 yyyy-MM(限制每月1次/全年12次)',
  pause_days INT DEFAULT NULL COMMENT '停卡天数',
  start_time DATETIME NOT NULL,
  end_time DATETIME DEFAULT NULL COMMENT '恢复时间(NULL=停卡中)',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0停卡中 1已恢复 2已取消',
  created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (pause_id), KEY idx_user (user_id), KEY idx_card (card_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停卡记录';
```

### 2.6 `member_blacklist` — 会员黑名单

用途：记录后台拉黑会员的原因与操作留痕，供转让前置/审核校验拦截（与 `user_info.auditStatus=2` 封禁并行）。`status=1` 为生效中的拉黑。索引：主键 `id`；`idx_user(user_id)`。

```sql
CREATE TABLE member_blacklist (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  reason VARCHAR(512) DEFAULT NULL,
  operator VARCHAR(64) DEFAULT NULL COMMENT '操作管理员',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1生效 0已解除',
  created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id), KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员黑名单';
```

### 2.7 现有表改造：`store` 增加门店等级

用途：给门店增加等级字段，越大越高；转让校验「转让人门店等级 ≥ 受让人当前门店等级」。

```sql
ALTER TABLE store ADD COLUMN store_level INT NOT NULL DEFAULT 1 COMMENT '门店等级,越大越高';
```

---

## 3. 命名、枚举与状态码

### 3.1 订单号末位后缀常量（`ConfigConstant` 新增）

现有后缀已占用：`CARD_ORDER_TYPE="2"`、`GOODS_ORDER_TYPE="3"`、`PRIVATE_CLASS_ORDER_TYPE="4"`、`TEAM_CLASS_ORDER_TYPE="5"`。新增两个单字符后缀须避开上述取值，取下一可用字符 `6`/`7`：

| 常量名 | 建议取值 | 用途 | 冲突检查 |
|---|---|---|---|
| `VIP_CARD_BUY_TYPE` | `"6"` | 权益卡购买订单 | 不与 2/3/4/5 冲突 |
| `VIP_TRANSFER_FEE_TYPE` | `"7"` | 权益转让服务费订单 | 不与 2/3/4/5 冲突 |

> 回调 `wxNotify` 按 `orderNo` 末位后缀 if/else 分发，需为 `6`/`7` 各加一个 else-if 分支。

### 3.2 状态枚举一览

| 表/字段 | 取值 → 含义 |
|---|---|
| `vip_benefit.status` | 9 待支付 / 0 正常 / 1 已转出失效 / 2 已冻结 / 3 已过期 |
| `vip_benefit_transfer.status` | 10 待付费 / 20 待审核 / 31 已驳回 / 40 待受让人确认 / 51 已拒绝 / 52 已超时 / 60 已撤回 / 70 已生效 |
| `vip_benefit_card.status` | 1 上架 / 2 下架 |
| `vip_fee_rule.status` | 1 启用 / 0 停用 |
| `card_pause_record.status` | 0 停卡中 / 1 已恢复 / 2 已取消 |
| `member_blacklist.status` | 1 生效 / 0 已解除 |

### 3.3 转让状态机流转（`vip_benefit_transfer.status`）

| From | 触发动作 | To | 退费 |
|---|---|---|---|
| —（新建） | 转让人发起申请 | 10 待付费 | — |
| 10 待付费 | 转让人微信支付服务费成功 | 20 待审核 | — |
| 20 待审核 | 后台审核驳回 | 31 已驳回 | 全额原路退 |
| 20 待审核 | 后台审核通过（推送受让人） | 40 待受让人确认 | — |
| 40 待受让人确认 | 受让人拒绝 | 51 已拒绝 | 全额原路退 |
| 40 待受让人确认 | 确认超时（定时任务） | 52 已超时 | 全额原路退 |
| 40 待受让人确认 | 转让人撤回 | 60 已撤回 | 不退 |
| 40 待受让人确认 | 受让人确认接收（不付费） | 70 已生效 | — |

### 3.4 `CodeAndMsg` 新增码（从 -50 起，避开现有 -1~-36 及 101/102/103/104）

| 枚举名 | 码值 | 文案（建议） |
|---|---|---|
| `ERROR_VIP_CARD_NOT_EXIST` | -50 | 权益卡不存在或已下架 |
| `ERROR_VIP_CARD_OFF_SHELF` | -51 | 权益卡已下架 |
| `ERROR_VIP_BENEFIT_NOT_EXIST` | -52 | 权益不存在 |
| `ERROR_VIP_BENEFIT_NOT_OWNER` | -53 | 该权益不属于当前用户 |
| `ERROR_VIP_BENEFIT_EXPIRED` | -54 | 权益已过期 |
| `ERROR_VIP_BENEFIT_NOT_NORMAL` | -55 | 权益状态异常,不可转让 |
| `ERROR_VIP_NOT_TRANSFERABLE` | -56 | 该权益不可转让 |
| `ERROR_VIP_TO_USER_NOT_EXIST` | -57 | 受让人不存在 |
| `ERROR_VIP_TO_USER_SELF` | -58 | 不能转让给自己 |
| `ERROR_VIP_USER_BLACKLIST` | -59 | 会员在黑名单,不可转让 |
| `ERROR_VIP_STORE_LEVEL_LOW` | -60 | 门店等级不满足:仅高级可向低级转让 |
| `ERROR_VIP_TRANSFER_NOT_EXIST` | -61 | 转让申请不存在 |
| `ERROR_VIP_TRANSFER_STATUS` | -62 | 转让申请状态不允许该操作 |
| `ERROR_VIP_FEE_RULE_NOT_EXIST` | -63 | 转让费用规则不存在 |
| `ERROR_VIP_PAUSE_MONTH_LIMIT` | -64 | 本月停卡次数已用尽 |
| `ERROR_VIP_PAUSE_YEAR_LIMIT` | -65 | 全年停卡次数已用尽 |

---

## 4. 复用现有能力清单

| 能力 | 现有签名 / 位置 | 如何用 |
|---|---|---|
| 微信下单(返回前端调起参数) | `PayService.wxRechargePay(BigDecimal money, String orderNo, Integer notifyType): SortedMap` | 权益卡购买、转让服务费走微信下单,返回前端调起参数 |
| 小程序 JSAPI 下单 | `PayService.wxPay(String orderNo, BigDecimal money, String openId, String notifyUrl): SortedMap` | 需 openId 的 JSAPI 场景下单 |
| 微信退款 | `PayService.wxRefund(Map<String,Object> params): String` | 驳回/拒绝/超时全额原路退服务费 |
| 查订单/更新订单状态 | `PayService.queryOrderInfoOrderNo(orderNo)`；`PayService.updateOrderStatus(orderNo, params, payType)` | 回调对账与订单状态更新 |
| 下单入口 | `PayController.rechargePay(payType, money, orderNo, req)` | 移动端发起支付的统一入口 |
| 支付回调 | `rechargeCallBack` = `POST /api/pay/wxNotify`：判 `return_code/result_code=SUCCESS` → `saveIncomePayDetail(orderNo, transaction_id, wallet, WXPAY)` → 按 `orderNo` 末位后缀 if/else 分发 | 新订单类型(后缀 `6`/`7`)在此 **新增 else-if 分支**,分别分发到权益卡购买与转让服务费的回调处理 |
| 记账 | `incomePayDetailService.saveIncomePayDetail(orderNo, transactionNumber, money, payType)`；`income_pay_detail` 字段含 `userId,storeId,orderNo,transactionNumber,payType,money,tradeDate,tradeType,openId,anotherId,tradeStatus,...` | 交易用途靠 `orderNo` 后缀区分；`anotherId` 记对方 `userId`；`payType` 对齐现有实现(回调传支付渠道 `WXPAY=2`) |
| 订单号生成 | `OrderNoGenerator.getOrderIdByTime()` | 生成单号后拼 `VIP_CARD_BUY_TYPE`/`VIP_TRANSFER_FEE_TYPE` 末位后缀 |
| 审核范式 | `SysIncomePayDetailController.updateTradeStatus`；教练 `coach.approveStatus/approveTime/approveResult` | 后台转让审核(通过/驳回)仿此实现,审核留痕写 `audit_user_id/audit_time/audit_remark` |
| 推送 | `com.dlc.modules.qd.UPushUntils.UMengPush.sendAndroidUnicast / sendIOSUnicast`；站内信 `system_msg` 表(`systemMsgService`) | 转让各节点(发起/通过/驳回/待确认/超时/生效)推送 + 站内信 |
| sys CRUD 范式 | `SysFitCardController` `@RequestMapping("sys/fitcard")`,方法 `/list /info/{id} /save /update /delete (+ /onCard /offCard)`；配套 `SysFitCardService`、`SysFitCardServiceImpl`、`FitCardDao`、`mapper/sys/FitCardDao.xml`、entity | 权益卡商品、转让费用规则等后台管理仿此一套范式实现 |
| 会员封禁标记 | `user_info.auditStatus=2`（`BaseController.getUserVo` 校验,命中抛 `ERROR_USER_IS_LOCK`） | 转让前置校验封禁;黑名单另查 `member_blacklist` |
| 身份获取 | `BaseController.getUserId(req)` / `getUserVo(req)` | api 接口取登录身份;需校验封禁的用 `getUserVo` |
## 5. 移动端 API 接口详细设计

> 所属模块：`com.dlc.modules.api`。统一返回 `R.reOk(data)`(code=1) / `R.reError(CodeAndMsg)`(code=0 或对应负码)。
> 身份获取：仅需 userId 的接口用 `getUserId(req)`；涉及资金/转让/封禁校验的接口用 `getUserVo(req)`（内部校验 `auditStatus=2` 封禁，命中抛 `ERROR_USER_IS_LOCK`=102）。
> token 取法沿用 `BaseController`：先读 header `token`，为空再读参数 `token`。
> 下单口径：购买/服务费均复用 `PayController.rechargePay(payType, money, orderNo, req)` 走微信下单，订单号由 `OrderNoGenerator.getOrderIdByTime()` 生成后拼末位后缀字符（`VIP_CARD_BUY_TYPE`="6"、`VIP_TRANSFER_FEE_TYPE`="7"，避开现有 2/3/4/5）。
> 金额一律后端重算，不信前端：`currentPrice` 按当前 `sold_count` 实时算，`service_fee` 按 `transfer_count+1` 命中分档算。
> Mapper XML（`mapper/api/`）改动需重启 Tomcat 才生效。

### 5.0 本章用到的 CodeAndMsg（新增码，从 -50 起）

| 枚举名 | code | msg |
|---|---|---|
| `ERROR_VIP_CARD_NOT_EXIST` | -50 | 权益卡不存在或已下架 |
| `ERROR_VIP_BENEFIT_NOT_EXIST` | -51 | 权益不存在 |
| `ERROR_VIP_BENEFIT_NOT_OWNER` | -52 | 该权益不属于你 |
| `ERROR_VIP_BENEFIT_EXPIRED` | -53 | 权益已过期 |
| `ERROR_VIP_BENEFIT_NOT_NORMAL` | -54 | 权益状态异常，不可转让 |
| `ERROR_VIP_NOT_TRANSFERABLE` | -55 | 该权益不可转让 |
| `ERROR_VIP_TO_USER_NOT_EXIST` | -56 | 受让人不存在 |
| `ERROR_VIP_TO_USER_SELF` | -57 | 不能转让给自己 |
| `ERROR_VIP_STORE_LEVEL_LIMIT` | -58 | 转让人门店等级低于受让人，不可转让 |
| `ERROR_VIP_USER_BLACKLIST` | -59 | 转让人或受让人在黑名单中 |
| `ERROR_VIP_FEE_RULE_NOT_EXIST` | -60 | 转让费用规则未配置 |
| `ERROR_VIP_TRANSFER_NOT_EXIST` | -61 | 转让单不存在 |
| `ERROR_VIP_TRANSFER_STATUS` | -62 | 转让单当前状态不允许此操作 |
| `ERROR_VIP_TRANSFER_NOT_FROM` | -63 | 非本转让单的转让人 |
| `ERROR_VIP_TRANSFER_NOT_TO` | -64 | 非本转让单的受让人 |
| `ERROR_VIP_CONFIRM_TIMEOUT` | -65 | 确认已超时 |
| `ERROR_VIP_PAUSE_MONTH_LIMIT` | -66 | 本月停卡次数已达上限 |
| `ERROR_VIP_PAUSE_YEAR_LIMIT` | -67 | 本年度停卡次数已达上限 |
| `ERROR_VIP_PAUSE_CARD_NOT_EXIST` | -68 | 卡不存在或不可停卡 |

复用现有码：`ERROR_LACK_PARAM`(-1)、`ERROR_USER_NOT_LOGIN`(101)、`ERROR_USER_IS_LOCK`(102)、`ERROR_PAY_ERROR`(-26)。

---

### 5.1 权益卡商品列表

- 方法+路径：`GET /api/vipCard/list`
- 是否需登录：否（可匿名浏览，用 `getUserVoIgnore(req)` 仅用于个性化，缺省可不取身份）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| storeAddrId | Long | 否 | 按适用门店过滤（匹配 `vip_benefit_card.store_addr_ids` 逗号包含） |
| page | Integer | 否 | 页码，默认 1 |
| limit | Integer | 否 | 每页条数，默认 10 |

- 出参（JSON 示例）：
```json
{
  "code": 1, "msg": "成功",
  "data": {
    "totalCount": 2, "pageSize": 10, "currPage": 1,
    "list": [
      {
        "vipCardId": 1, "cardName": "金牌年卡", "benefitDesc": "全门店通用...",
        "price": 199.00, "currentPrice": 249.00, "soldCount": 326,
        "showBuyCount": 1, "validityDays": 365, "status": 1
      }
    ]
  }
}
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)
- 逻辑说明：分页查 `status=1`（上架）的权益卡，`currentPrice` 按动态购买价公式用当前 `sold_count` 实时算，`soldCount` 仅当 `show_buy_count=1` 时返回（否则置 0 或不返回）。

---

### 5.2 权益卡商品详情

- 方法+路径：`GET /api/vipCard/detail`
- 是否需登录：否
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| vipCardId | Long | 是 | 权益卡商品ID |

- 出参（JSON 示例）：
```json
{
  "code": 1, "msg": "成功",
  "data": {
    "vipCardId": 1, "cardName": "金牌年卡", "benefitDesc": "全门店通用...",
    "price": 199.00, "currentPrice": 249.00, "soldCount": 326, "showBuyCount": 1,
    "validityDays": 365, "storeAddrIds": "12,15", "feeRuleId": 3, "status": 1
  }
}
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)、`ERROR_VIP_CARD_NOT_EXIST`(-50)
- 逻辑说明：查单条上架权益卡，`currentPrice` 后端按 `sold_count` 实时算，`soldCount` 受 `show_buy_count` 控制是否展示。

---

### 5.3 购买权益卡（下单）

- 方法+路径：`POST /api/vipCard/buy`
- 是否需登录：是（`getUserVo(req)`，校验封禁）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| vipCardId | Long | 是 | 权益卡商品ID |
| storeAddrId | Long | 否 | 购买/归属门店地址ID（用于落 `vip_benefit.store_addr_id`/`store_id`） |

- 出参（JSON 示例，微信下单调起参数，沿用 `wxRechargePay` 返回结构）：
```json
{
  "code": 1, "msg": "成功",
  "data": {
    "orderNo": "202606271015301234567896",
    "appId": "wx...", "timeStamp": "1719456930", "nonceStr": "...",
    "package": "prepay_id=wx...", "signType": "MD5", "paySign": "..."
  }
}
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)、`ERROR_USER_IS_LOCK`(102)、`ERROR_VIP_CARD_NOT_EXIST`(-50)、`ERROR_PAY_ERROR`(-26)
- 逻辑说明：服务端按当前 `sold_count` 重算 `currentPrice` 作为应付金额，生成订单号（末位拼 `VIP_CARD_BUY_TYPE`="6"），先落一条 `vip_benefit`（`status=9` 待支付、`origin_price=currentPrice`、`origin_user_id=user_id`、`source_order_no` 关联），再调微信下单返回前端调起参数。支付成功回调（`/api/pay/wxNotify` 按后缀 "6" 分发）里：写 `income_pay_detail`、把 `vip_benefit.status` 9→0、`start_time=now`/`expire_time=now+validity_days`、`vip_benefit_card.sold_count+1`（仅购买 +1，转让不 +）；回调按 `source_order_no` 去重保证幂等。

---

### 5.4 我持有的权益卡

- 方法+路径：`GET /api/vipCard/myBenefits`
- 是否需登录：是（`getUserId(req)`）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| status | Integer | 否 | 过滤持有状态（不传=默认查 0正常）；可传 0/2/3 |
| page | Integer | 否 | 页码，默认 1 |
| limit | Integer | 否 | 每页条数，默认 10 |

- 出参（JSON 示例）：
```json
{
  "code": 1, "msg": "成功",
  "data": {
    "totalCount": 1, "pageSize": 10, "currPage": 1,
    "list": [
      {
        "vipBenefitId": 1001, "vipCardId": 1, "cardName": "金牌年卡",
        "startTime": "2026-01-01 00:00:00", "expireTime": "2026-12-31 23:59:59",
        "status": 0, "transferCount": 1, "transferable": 1, "originPrice": 199.00
      }
    ]
  }
}
```

- 可能返回码：`ERROR_USER_NOT_LOGIN`(101)
- 逻辑说明：查 `vip_benefit.user_id=当前userId` 的持有实例，关联 `vip_benefit_card.card_name`；到期判断实时比较 `expire_time<now`（过期实际状态按 3 展示），`transferable=0` 或非 0 状态前端置灰转让入口。

---

### 5.5 试算转让费用

- 方法+路径：`POST /api/vipTransfer/quote`
- 是否需登录：是（`getUserId(req)`）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| vipBenefitId | Long | 是 | 待转让权益实例ID |

- 出参（JSON 示例）：
```json
{
  "code": 1, "msg": "成功",
  "data": { "vipBenefitId": 1001, "transferCount": 1, "thisTransferNo": 2, "serviceFee": 50.00 }
}
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)、`ERROR_VIP_BENEFIT_NOT_EXIST`(-51)、`ERROR_VIP_BENEFIT_NOT_OWNER`(-52)、`ERROR_VIP_FEE_RULE_NOT_EXIST`(-60)
- 逻辑说明：校验权益属当前用户，取该卡 `fee_rule_id` 对应 `vip_fee_rule.tiers_json`，按本次转让次数 = `transfer_count+1`（即 `thisTransferNo`），在 tiers 中取 `fromCount<=本次` 的最大档 `fee`，无匹配=0；只试算不落单、不收费。

---

### 5.6 发起转让

- 方法+路径：`POST /api/vipTransfer/apply`
- 是否需登录：是（`getUserVo(req)`，校验转让人封禁）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| vipBenefitId | Long | 是 | 待转让权益实例ID |
| toUserId | Long | 是 | 受让人 userId（或前端先按手机号换到 userId 后传入） |

- 出参（JSON 示例）：
  - 当 `serviceFee>0`，返回转让单 + 微信下单调起参数（转让人先付服务费）：
```json
{
  "code": 1, "msg": "成功",
  "data": {
    "transferId": 5001, "status": 10, "serviceFee": 50.00,
    "serviceFeeOrderNo": "202606271018221234567897",
    "pay": { "appId": "wx...", "timeStamp": "1719457102", "nonceStr": "...",
             "package": "prepay_id=wx...", "signType": "MD5", "paySign": "..." }
  }
}
```
  - 当 `serviceFee=0`（首档免费），无需支付，直接进入待审核：
```json
{ "code": 1, "msg": "成功", "data": { "transferId": 5001, "status": 20, "serviceFee": 0.00 } }
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)、`ERROR_USER_IS_LOCK`(102)、`ERROR_VIP_BENEFIT_NOT_EXIST`(-51)、`ERROR_VIP_BENEFIT_NOT_OWNER`(-52)、`ERROR_VIP_BENEFIT_EXPIRED`(-53)、`ERROR_VIP_BENEFIT_NOT_NORMAL`(-54)、`ERROR_VIP_NOT_TRANSFERABLE`(-55)、`ERROR_VIP_TO_USER_NOT_EXIST`(-56)、`ERROR_VIP_TO_USER_SELF`(-57)、`ERROR_VIP_STORE_LEVEL_LIMIT`(-58)、`ERROR_VIP_USER_BLACKLIST`(-59)、`ERROR_VIP_FEE_RULE_NOT_EXIST`(-60)、`ERROR_PAY_ERROR`(-26)
- 逻辑说明：发起前置校验（权益属本人且 `status=0`/未过期、`transferable=1`、转让人/受让人均不在 `member_blacklist`、双方 `auditStatus!=2`、转让人门店 `store_level>=` 受让人门店 `store_level`、不能转给自己）；后端按 `transfer_count+1` 重算 `service_fee`，建 `vip_benefit_transfer`。`service_fee>0`→`status=10`待付费，生成服务费单号（末位 `VIP_TRANSFER_FEE_TYPE`="7"）写入 `service_fee_order_no` 并返回微信下单参数；服务费支付成功回调（按后缀 "7" 分发）把 `status` 10→20待审核。`service_fee=0`→直接 `status=20`。审核通过流转见 5.8/5.9，确认生效是后台审核通过后由受让人 confirm 触发，过户单事务完成。

---

### 5.7 我的转让/受让记录

- 方法+路径：`GET /api/vipTransfer/myList`
- 是否需登录：是（`getUserId(req)`）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| role | Integer | 否 | 1=我发起的(转让人) 2=我接收的(受让人)，不传=全部 |
| status | Integer | 否 | 按转让单状态过滤（10/20/31/40/51/52/60/70） |
| page | Integer | 否 | 页码，默认 1 |
| limit | Integer | 否 | 每页条数，默认 10 |

- 出参（JSON 示例）：
```json
{
  "code": 1, "msg": "成功",
  "data": {
    "totalCount": 1, "pageSize": 10, "currPage": 1,
    "list": [
      {
        "transferId": 5001, "vipBenefitId": 1001, "cardName": "金牌年卡",
        "fromUserId": 200, "toUserId": 300, "serviceFee": 50.00,
        "status": 40, "confirmDeadline": "2026-06-29 10:18:22",
        "auditRemark": null, "refundStatus": 0,
        "createdDate": "2026-06-27 10:18:22", "effectTime": null
      }
    ]
  }
}
```

- 可能返回码：`ERROR_USER_NOT_LOGIN`(101)
- 逻辑说明：按 `role` 用 `from_user_id` / `to_user_id` 匹配当前 userId 查 `vip_benefit_transfer`，关联卡名；前端据 `status` 渲染对应可操作按钮（撤回/确认/拒绝）。

---

### 5.8 转让人撤回

- 方法+路径：`POST /api/vipTransfer/withdraw`
- 是否需登录：是（`getUserId(req)`）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| transferId | Long | 是 | 转让单ID |

- 出参（JSON 示例）：
```json
{ "code": 1, "msg": "成功", "data": { "transferId": 5001, "status": 60 } }
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)、`ERROR_VIP_TRANSFER_NOT_EXIST`(-61)、`ERROR_VIP_TRANSFER_NOT_FROM`(-63)、`ERROR_VIP_TRANSFER_STATUS`(-62)
- 逻辑说明：仅转让人本人可撤回，且仅在 `status=40`（审核通过、待受让人确认）阶段撤回，置 `status=60` 已撤回。按 D-3/R-11：审核通过后撤回**服务费不退**（`refund_status` 保持 0）。

---

### 5.9 受让人确认接收

- 方法+路径：`POST /api/vipTransfer/confirm`
- 是否需登录：是（`getUserVo(req)`，校验受让人封禁）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| transferId | Long | 是 | 转让单ID |

- 出参（JSON 示例）：
```json
{ "code": 1, "msg": "成功", "data": { "transferId": 5001, "status": 70, "effectTime": "2026-06-27 11:00:00" } }
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)、`ERROR_USER_IS_LOCK`(102)、`ERROR_VIP_TRANSFER_NOT_EXIST`(-61)、`ERROR_VIP_TRANSFER_NOT_TO`(-64)、`ERROR_VIP_TRANSFER_STATUS`(-62)、`ERROR_VIP_CONFIRM_TIMEOUT`(-65)
- 逻辑说明：仅受让人本人、仅 `status=40` 且 `now<=confirm_deadline` 可确认（**不付费**）。命中后在 `api.service.impl` 单事务内完成过户：`vip_benefit.user_id` 改为受让人、`transfer_count+1`（`transferable` 保持 1）、原持有人该权益 `status` 置 1 已转出失效、`expire_time` 不变（继承剩余有效期，到期日不重置）；转让单 `status`→70、`effect_time=now`；写 `income_pay_detail`（转让人服务费收入，`anotherId` 记受让人 userId，`payType` 取 WXPAY=2）；私教课时/储值/运动档案不动；推送双方"转让完成"。`sold_count` 不增。

---

### 5.10 受让人拒绝

- 方法+路径：`POST /api/vipTransfer/reject`
- 是否需登录：是（`getUserId(req)`）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| transferId | Long | 是 | 转让单ID |

- 出参（JSON 示例）：
```json
{ "code": 1, "msg": "成功", "data": { "transferId": 5001, "status": 51 } }
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)、`ERROR_VIP_TRANSFER_NOT_EXIST`(-61)、`ERROR_VIP_TRANSFER_NOT_TO`(-64)、`ERROR_VIP_TRANSFER_STATUS`(-62)
- 逻辑说明：仅受让人本人、仅 `status=40` 可拒绝，置 `status=51` 已拒绝；若已收服务费则触发全额原路退款（复用 `PayService.wxRefund`，按 `service_fee_order_no` 退，`refund_status` 0→1），不动权益归属。

---

### 5.11 停卡申请

- 方法+路径：`POST /api/cardPause/apply`
- 是否需登录：是（`getUserId(req)`）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| cardOrderId | Long | 是 | 被停的会员卡（`card_order`）ID |
| pauseDays | Integer | 否 | 停卡天数（按 D-5，单次时长上限待业务给，不传则按默认配置） |

- 出参（JSON 示例）：
```json
{
  "code": 1, "msg": "成功",
  "data": { "pauseId": 9001, "cardOrderId": 700, "pauseMonth": "2026-06",
            "pauseDays": 7, "startTime": "2026-06-27 11:05:00", "status": 0 }
}
```

- 可能返回码：`ERROR_LACK_PARAM`(-1)、`ERROR_VIP_PAUSE_CARD_NOT_EXIST`(-68)、`ERROR_VIP_PAUSE_MONTH_LIMIT`(-66)、`ERROR_VIP_PAUSE_YEAR_LIMIT`(-67)
- 逻辑说明：按 D-5 校验该卡当前自然月 `pause_month`(yyyy-MM) 已停 1 次则拒、当年累计 12 次则拒；通过则写 `card_pause_record`（`status=0`停卡中、`start_time=now`、`pause_month`=当前月），停卡期间有效期顺延、不可入场，无需后台审核（会员自助即时生效）；到期恢复由实时判断/定时任务把 `status`→1 并顺延 `expire_time`。

---

### 5.12 停卡记录列表

- 方法+路径：`GET /api/cardPause/list`
- 是否需登录：是（`getUserId(req)`）
- 入参：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| cardOrderId | Long | 否 | 按卡过滤，不传=查我全部停卡记录 |
| page | Integer | 否 | 页码，默认 1 |
| limit | Integer | 否 | 每页条数，默认 10 |

- 出参（JSON 示例）：
```json
{
  "code": 1, "msg": "成功",
  "data": {
    "totalCount": 1, "pageSize": 10, "currPage": 1,
    "list": [
      {
        "pauseId": 9001, "cardOrderId": 700, "pauseMonth": "2026-06",
        "pauseDays": 7, "startTime": "2026-06-27 11:05:00",
        "endTime": null, "status": 0, "createdDate": "2026-06-27 11:05:00"
      }
    ]
  }
}
```

- 可能返回码：`ERROR_USER_NOT_LOGIN`(101)
- 逻辑说明：查 `card_pause_record.user_id=当前userId` 的停卡记录（可按 `card_order_id` 过滤），`status=0` 表示停卡中、1已恢复、2已取消；前端据此展示剩余次数（本月/全年）与当前停卡状态。
## 6. 后台 sys 接口详细设计

本章覆盖后台管理端（`com.dlc.modules.sys`）全部新增/改造接口。统一约定：

- 返回值一律 `R`：成功 `R.ok()`（可链式 `.put("key", val)`），失败 `R.error("提示文案")`。
- 鉴权一律 `@RequiresPermissions("sys:xxx:yyy")`；权限串需在 `sys_menu`/`sys_role_menu` 体系内配好。
- 列表接口入参用 `@RequestParam Map<String,Object> params`，内部 `Query query = new Query(params)` + `PageUtils`；写接口入参用 `@RequestBody`。
- service.impl 下所有方法自动 REQUIRED 事务，审核/退费的多表写在一个 service 方法里完成即可单事务。
- 操作人取 `ShiroUtils.getUserEntity()`（用户名/ID），用于 `member_blacklist.operator`、`vip_benefit_transfer.audit_user_id`。
- sys 的 Mapper XML 改动热刷新，无需重启 Tomcat。

新增控制器：`SysVipCardController`、`SysVipFeeRuleController`、`SysVipTransferController`、`SysMemberBlacklistController`、`SysCardPauseController`；改造 `SysStoreController`（增 `store_level`）。

---

### 6.1 权益卡商品 `SysVipCardController`（`@RequestMapping("sys/vipCard")`）

对应表 `vip_benefit_card`。`sold_count` 为系统维护字段，后台**只读、不可写**：`save`/`update` 一律忽略前端传入的 `soldCount`，由 service 在落库前强制剔除（update 用指定列更新或在 XML 里不写该列），杜绝后台篡改真实购买人数。动态定价相关字段（`base_buy_count`/`step_num`/`step_add_price`/`price_cap`/`show_buy_count`）由本控制器维护。

实体：`VipBenefitCardEntity`（`com.dlc.modules.sys.entity`），字段一一对应 DDL；service `SysVipCardService`/`SysVipCardServiceImpl`，dao `VipBenefitCardDao`，XML `mapper/sys/VipBenefitCardDao.xml`。

| 方法+路径 | 权限串 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| `GET/POST /sys/vipCard/list` | `sys:vipcard:list` | `params`：`cardName`(模糊)、`status`(1上架/2下架)、`page`、`limit` | `R.ok().put("page", PageUtils)` | 分页查询权益卡商品。列表项含 `soldCount`（只读展示真实购买人数）、`price`、动态定价字段。 |
| `GET /sys/vipCard/info/{id}` | `sys:vipcard:info` | `id`(vip_card_id, PathVariable) | `R.ok().put("vipCard", VipBenefitCardEntity)` | 详情回显，含所有动态定价字段、`feeRuleId`、`soldCount`。 |
| `POST /sys/vipCard/save` | `sys:vipcard:save` | `@RequestBody VipBenefitCardEntity`：`cardName`、`benefitDesc`、`price`、`validityDays`、`storeAddrIds`、`feeRuleId`、`showBuyCount`、`baseBuyCount`、`stepNum`、`stepAddPrice`、`priceCap`、`status` | `R.ok()` | 新增上架商品。service 内 `setCreatedDate(new Date())`、强制 `soldCount=0`（忽略前端值）、`status` 默认 1。`feeRuleId` 可空（空=转让免费）。校验 `price>=0`、`stepNum>=0`。 |
| `POST /sys/vipCard/update` | `sys:vipcard:update` | `@RequestBody VipBenefitCardEntity`（含 `vipCardId`） | `R.ok()` | 全量更新基础信息与动态定价字段；**`soldCount` 不参与更新**（service 剔除）。动态价由下单时按当前 `soldCount` 重算，改这些字段只影响后续涨价曲线。 |
| `POST /sys/vipCard/delete` | `sys:vipcard:delete` | `@RequestBody Long[] vipCardIds` | `R.ok()` / `R.error("该权益卡已售出，不能删除")` | 删除前逐个校验 `sold_count>0` 或存在关联 `vip_benefit` 记录则拒删（仿 `SysFitCardController.delete` 的购买计数拦截）。 |
| `POST /sys/vipCard/onCard` | `sys:vipcard:update` | `@RequestBody Long[] vipCardIds` | `R.ok()` | 批量上架：逐个 `status=1`（仿 `SysFitCardController.onCard`，复用 `sys:vipcard:update` 权限）。 |
| `POST /sys/vipCard/offCard` | `sys:vipcard:update` | `@RequestBody Long[] vipCardIds` | `R.ok()` | 批量下架：逐个 `status=2`。下架仅停售，不影响已售出的 `vip_benefit` 实例与转让流程。 |

---

### 6.2 转让费用规则 `SysVipFeeRuleController`（`@RequestMapping("sys/vipFeeRule")`）

对应表 `vip_fee_rule`。分档以 `tiers_json` 存储，形如 `[{"fromCount":1,"fee":0},{"fromCount":2,"fee":50},{"fromCount":3,"fee":100}]`，约定 `fromCount` 升序、定额 `fee`、最后一档兜底所有更高次数（D-4）。`vip_benefit_card.fee_rule_id` 引用本表。

实体：`VipFeeRuleEntity`；service `SysVipFeeRuleService`/Impl，dao `VipFeeRuleDao`，XML `mapper/sys/VipFeeRuleDao.xml`。

| 方法+路径 | 权限串 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| `GET/POST /sys/vipFeeRule/list` | `sys:vipfeerule:list` | `params`：`ruleName`(模糊)、`status`(1启用/0停用)、`page`、`limit` | `R.ok().put("page", PageUtils)` | 分页查询费用规则；列表项含 `tiersJson` 原文供前端解析展示分档。 |
| `GET /sys/vipFeeRule/info/{id}` | `sys:vipfeerule:info` | `id`(fee_rule_id) | `R.ok().put("vipFeeRule", VipFeeRuleEntity)` | 详情回显，前端把 `tiersJson` 反序列化成档位表格编辑。 |
| `POST /sys/vipFeeRule/save` | `sys:vipfeerule:save` | `@RequestBody VipFeeRuleEntity`：`ruleName`、`tiersJson`、`status` | `R.ok()` / `R.error("分档配置格式有误")` | 新增。service 校验 `tiersJson` 合法 JSON 数组、`fromCount` 为正整数、`fee>=0`、按 `fromCount` 升序去重；`setCreatedDate(new Date())`，`status` 默认 1。 |
| `POST /sys/vipFeeRule/update` | `sys:vipfeerule:update` | `@RequestBody VipFeeRuleEntity`（含 `feeRuleId`） | `R.ok()` / `R.error("分档配置格式有误")` | 全量更新；同样校验 `tiersJson`。改规则只影响**后续新发起**的转让命中，已发起单的 `service_fee` 不回溯。 |
| `POST /sys/vipFeeRule/delete` | `sys:vipfeerule:delete` | `@RequestBody Long[] feeRuleIds` | `R.ok()` / `R.error("该费用规则已被权益卡引用，不能删除")` | 删除前校验是否被 `vip_benefit_card.fee_rule_id` 引用，被引用则拒删（避免商品悬空引用）。 |

---

### 6.3 转让审核 `SysVipTransferController`（`@RequestMapping("sys/vipTransfer")`）

对应表 `vip_benefit_transfer`（状态机：10待付费/20待审核/31已驳回/40待受让人确认/51已拒绝/52已超时/60已撤回/70已生效）。后台只处理「20待审核」单：通过→40待受让人确认（写 `confirm_deadline` + 推送受让人），驳回→31已驳回（微信原路退服务费 + 推送转让人）。审核范式仿 `SysCoachController.updateSuccess`（先校验当前状态再流转、写审核人/时间）与 `SysIncomePayDetailController.updateTradeStatus`。

实体：`VipBenefitTransferEntity`；service `SysVipTransferService`/Impl，dao `VipBenefitTransferDao`，XML `mapper/sys/VipBenefitTransferDao.xml`。退费复用 `PayService.wxRefund(Map)`（按 `service_fee_order_no`/`transaction_number` 原路退），推送复用 `UPushUntils.UMengPush.sendAndroidUnicast/sendIOSUnicast` + 站内信 `systemMsgService`。

| 方法+路径 | 权限串 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| `GET/POST /sys/vipTransfer/list` | `sys:viptransfer:list` | `params`：`status`(可空，按状态筛：20待审核/40待确认/31已驳回/70已生效…)、`fromUserId`/`toUserId`/手机号、`storeIds`(由 `ShiroUtils.getUserEntity().getStoreIds()` 注入做门店数据权限)、`page`、`limit` | `R.ok().put("page", PageUtils)` | 审核工作台列表。列表项联表带出转让人/受让人昵称手机号、权益卡名称、`serviceFee`、`transferCount`、`createdDate`、`confirmDeadline`、`status`、`auditRemark`。 |
| `POST /sys/vipTransfer/audit` | `sys:viptransfer:audit` | `@RequestBody Map`：`transferId`(Long,必填)、`pass`(Boolean/Integer,1通过 0驳回,必填)、`remark`(String,驳回原因/备注) | `R.ok()` / `R.error("...")` | 单事务审核。流程见下。 |

**`audit` 处理逻辑（service `SysVipTransferServiceImpl.audit` 内单事务完成）：**

1. 校验：`transferId` 对应单存在且当前 `status==20`（待审核），否则 `R.error("请选择待审核的转让申请")`（仿 coach「请选择待审核的数据操作」）。
2. 写审核留痕：`audit_user_id = ShiroUtils.getUserEntity().getUserId()`、`audit_time = new Date()`、`audit_remark = remark`。
3. **通过分支（pass=1）**：
   - 复核前置校验规则集（权益未过期、双方未被封禁/拉黑、门店等级源≥目标等，与 api 发起侧同一套校验方法，避免审核期内状态变化放过非法单）；命中任一条则按驳回处理并写明原因。
   - `status → 40`（待受让人确认），写 `confirm_deadline = now + N`（确认时限，N 取配置项，如 3 天）。
   - 推送受让人：友盟单播 + 站内信「您有一份权益待确认接收」。`vip_benefit`/`sold_count` **此刻不动**（过户与计数发生在受让人确认那一刻，转让不增 `sold_count`）。
4. **驳回分支（pass=0）**：
   - `status → 31`（已驳回）。
   - 退费：调 `PayService.wxRefund`（按 `service_fee_order_no`、原 `transaction_number`、`service_fee` 原路退微信），成功后置 `refund_status=1`；退款记账复用 `incomePayDetailService.saveIncomePayDetail(...)`（退款流水，`anotherId` 记对方 userId）。
   - 同时回滚权益占用标记（如发起时把 `vip_benefit.status` 置过冻结/占用，则恢复为 `0正常`）。
   - 推送转让人：友盟单播 + 站内信「您的转让申请被驳回，服务费已原路退回」，附 `remark` 原因。
   - 退款失败：抛异常使本事务回滚、`status` 不流转，列表保留待审核/可重试，避免「已驳回但未退款」的脏状态。

> 说明：「受让人确认/转让人撤回/超时退费」属移动端 api 与定时任务职责（40→70/51/52/60），不在本后台章节；后台仅负责 20→40/31 这一步及只读查看全链路状态。

---

### 6.4 门店等级 `SysStoreController` 改造（`@RequestMapping("/sys/store")`，在现有控制器上加字段）

在现有 `SysStoreController` 的 `save`/`update`/`list`/`info` 上承载 `store.store_level`（DDL：`store_level INT NOT NULL DEFAULT 1`，越大等级越高，转让校验「源≥目标」用）。**不新增独立控制器**，仅在 `SysStoreEntity` 加 `storeLevel` 字段并打通既有 CRUD。

| 方法+路径 | 权限串 | 改动点 | 说明 |
|---|---|---|---|
| `POST /sys/store/save` | `sys:store:save` | 入参 `Map params` 增加读取 `storeLevel`（缺省 1），随 `insertStore(params)` 一并落库 | 新建门店时设置等级；空值默认 1。 |
| `POST /sys/store/update` | `sys:store:update` | 入参 `@RequestBody SysStoreEntity store` 增加 `storeLevel`，随 `sysStoreService.update(store)` 落库 | 维护已有门店等级；`store_address` 同步逻辑无需改动（等级只在 `store` 表）。 |
| `GET/POST /sys/store/list` | `sys:store:list` | 列表项 `SysStoreEntity` 增带 `storeLevel` 字段返回 | 列表/详情(`/info/{id}`) 回显等级，前端在门店编辑表单加「门店等级」整数输入项。 |

> 仅改 `SysStoreEntity`（加 `storeLevel`/getter/setter）、`mapper/sys/StoreDao.xml`（`insertStore`/`update`/resultMap 增列）；复用现有权限串，不新增权限。

---

### 6.5 会员黑名单 `SysMemberBlacklistController`（`@RequestMapping("sys/memberBlacklist")`）

对应表 `member_blacklist`（status：1生效/0已解除）。命中生效黑名单的会员在 api 发起/后台审核转让时被前置校验拦截（与 `auditStatus=2` 封禁并列）。

实体：`MemberBlacklistEntity`；service `SysMemberBlacklistService`/Impl，dao `MemberBlacklistDao`，XML `mapper/sys/MemberBlacklistDao.xml`。

| 方法+路径 | 权限串 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| `GET/POST /sys/memberBlacklist/list` | `sys:memberblacklist:list` | `params`：`userId`/会员手机号昵称(模糊)、`status`(1生效/0已解除)、`page`、`limit` | `R.ok().put("page", PageUtils)` | 分页查询黑名单；联表带出会员昵称/手机号、`reason`、`operator`、`createdDate`、`status`。 |
| `POST /sys/memberBlacklist/save` | `sys:memberblacklist:save` | `@RequestBody MemberBlacklistEntity`：`userId`(必填)、`reason` | `R.ok()` / `R.error("该会员已在黑名单中")` | 拉黑。service 校验该 `user_id` 是否已有 `status=1` 记录，已有则拒；否则插入 `status=1`、`operator = ShiroUtils.getUserEntity().getUsername()`、`setCreatedDate(new Date())`。 |
| `POST /sys/memberBlacklist/remove` | `sys:memberblacklist:remove` | `@RequestBody Long id`（黑名单记录 id；或 `userId`，二选一约定用记录 `id`） | `R.ok()` / `R.error("操作有误")` | 解除拉黑：将该记录 `status` 置 0（已解除），留痕不物理删除。 |

---

### 6.6 停卡记录只读查看 `SysCardPauseController`（`@RequestMapping("sys/cardPause")`）

对应表 `card_pause_record`（status：0停卡中/1已恢复/2已取消）。停卡为会员自助、**无需后台审核**（D-5：年卡每自然月 1 次、全年 12 次、有效期顺延），后台仅提供只读查看，不提供审批/增删改。

实体：`CardPauseRecordEntity`；service `SysCardPauseService`/Impl，dao `CardPauseRecordDao`，XML `mapper/sys/CardPauseRecordDao.xml`。

| 方法+路径 | 权限串 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| `GET/POST /sys/cardPause/list` | `sys:cardpause:list` | `params`：`userId`/会员手机号昵称(模糊)、`cardOrderId`、`pauseMonth`(yyyy-MM)、`status`、`storeIds`(门店数据权限)、`page`、`limit` | `R.ok().put("page", PageUtils)` | 只读分页查询停卡记录；联表带出会员、被停卡信息、`pauseMonth`、`pauseDays`、`startTime`、`endTime`、`status`、`createdDate`。仅查询接口，无 save/update/delete。 |

---

### 6.7 本章涉及的 CodeAndMsg 错误码（后台用 `R.error(文案)` 为主，下列为与 api 共用的语义对齐项，从 -50 起）

后台接口主要走 `R.error("中文文案")`，但审核 `audit` 复核前置校验时与 api 共用同一组业务码，便于统一文案：

- `ERROR_VIP_TRANSFER_NOT_AUDIT(-50, "请选择待审核的转让申请")`
- `ERROR_VIP_TRANSFER_REFUND_FAIL(-51, "服务费退款失败，请稍后重试")`
- `ERROR_VIP_BENEFIT_EXPIRED(-52, "权益已过期，不可转让")`
- `ERROR_VIP_USER_BLACKLIST(-53, "会员在黑名单中，不可转让")`
- `ERROR_VIP_STORE_LEVEL(-54, "门店等级不满足转让条件")`
- `ERROR_VIP_FEE_RULE_FORMAT(-55, "分档配置格式有误")`

（具体码值与 api 章节统一编号，避开现有 -1~-36；以最终汇总章为准。）
## 7. 核心流程与事务实现

本章给出权益卡购买、转让状态机、过户事务、退费、超时定时任务的 Service 方法签名与伪码。所有方法落在 `com.dlc.modules.api.service.impl`（命中事务切面，默认 `REQUIRED`，无需注解），单次方法调用即一个事务边界。幂等一律靠「带状态条件的 UPDATE + 受影响行数判定」实现；并发临界资源（被转让的 `vip_benefit` 行）一律先 `SELECT ... FOR UPDATE` 上行锁再改。

> 约定：下文 `>0` 指 MyBatis update 返回的受影响行数；命中 0 行代表「状态已不满足前置条件」（重复回调/重复确认/并发已处理），直接幂等返回、不报错、不重复记账。

### 7.1 新增订单号后缀常量（ConfigConstant）

```java
/** 权益卡购买订单后缀 */
public static final String VIP_CARD_BUY_TYPE = "6";       // 避开 2/3/4/5
/** 权益转让服务费订单后缀 */
public static final String VIP_TRANSFER_FEE_TYPE = "7";
```

回调按 `orderNo.substring(len-1)` 区分用途，`6`/`7` 与现有 `CARD=2 / GOODS=3 / PRIVATE_CLASS=4 / TEAM_CLASS=5` 不冲突。

### 7.2 权益卡购买

#### 7.2.1 下单（移动端 `POST /api/vipCard/buy`）

价格后端按当前 `sold_count` 重算，不信前端;先落 `vip_benefit`（`status=9` 待支付）占位、再下微信单。`source_order_no` 即作为后续回调的幂等键。

```java
// VipBenefitServiceImpl implements VipBenefitService
// 返回微信调起参数(wxRechargePay 的 SortedMap),前端拉起支付
SortedMap<String,String> buy(Long userId, Long vipCardId, HttpServletRequest req) throws Exception;
```

伪码：

```
buy(userId, vipCardId):
    card = vipBenefitCardMapper.selectById(vipCardId)
    校验 card != null 且 card.status == 1(上架)        // 否则 ERROR_VIP_CARD_OFF_SHELF
    // 动态购买价:严格按 §计算公式,以当前 sold_count 重算
    price = calcDynamicPrice(card)   // 见 7.2.2
    orderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.VIP_CARD_BUY_TYPE
    // 先建待支付权益实例占位(status=9),记 source_order_no 作幂等键
    vb = new VipBenefit()
    vb.userId = userId; vb.originUserId = userId
    vb.vipCardId = vipCardId; vb.sourceOrderNo = orderNo
    vb.storeId/storeAddrId = 解析自 card.storeAddrIds 或用户归属门店
    vb.originPrice = price
    vb.startTime = null; vb.expireTime = null   // 支付成功回调才生效
    vb.status = 9                                // 9 待支付
    vb.transferCount = 0; vb.transferable = 1
    vipBenefitMapper.insertSelective(vb)
    // 微信下单,notifyType 给 null(对齐现有 rechargePay 充值回调流程)
    return payService.wxRechargePay(price, orderNo, null)
```

> 移动端身份用 `getUserVo(req)`（封禁 `auditStatus=2` 直接抛 `ERROR_USER_IS_LOCK`）。下单仅微信，复用 `PayService.wxRechargePay`。

#### 7.2.2 动态购买价计算

```java
BigDecimal calcDynamicPrice(VipBenefitCard card);
```

```
calcDynamicPrice(card):
    n = card.soldCount
    if card.stepNum <= 0 || card.stepAddPrice <= 0: return card.price
    if n <= card.baseBuyCount:                      return card.price
    tier = floor((n - card.baseBuyCount) / card.stepNum)
    p = card.price + tier * card.stepAddPrice
    if card.priceCap != null: p = min(p, card.priceCap)
    return p
```

#### 7.2.3 微信回调激活（PayController.rechargeCallBack 新增 else-if 分支）

在 `rechargeCallBack` 现有 `if/else-if` 链末尾按后缀加分支，调用激活方法。激活 = 幂等条件更新 `vip_benefit` + 原子 `sold_count+1`，同一事务完成。

```java
// PayController 内,saveIncomePayDetail 之后:
} else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_CARD_BUY_TYPE)) {
    log.info("-------激活VIP权益卡=========================================");
    vipBenefitService.activateByOrderNo(orderNo, wallet, transaction_id, payType);
}
```

```java
int activateByOrderNo(String orderNo, BigDecimal money, String transactionNumber, Integer payType);
```

```
activateByOrderNo(orderNo, money, txNo, payType):
    now = new Date()
    card = vipBenefitCardMapper.selectByOrderNo(orderNo)   // 经 vip_benefit 关联取 validity_days
    expire = now + card.validityDays 天
    // 幂等核心:仅 status=9 时才激活;并发/重复回调命中 0 行直接返回
    rows = vipBenefitMapper.activate(orderNo, now, expire)
        // UPDATE vip_benefit SET status=0, start_time=#{now}, expire_time=#{expire}
        // WHERE source_order_no=#{orderNo} AND status=9
    if rows == 0: return 0                                  // 已激活过,幂等退出,不再 +1
    // 同一事务原子自增真实购买人数(仅购买 +1,转让不 +)
    vipBenefitCardMapper.incrSoldCount(card.vipCardId)
        // UPDATE vip_benefit_card SET sold_count = sold_count + 1 WHERE vip_card_id=#{id}
    return 1
```

> `saveIncomePayDetail(orderNo, txNo, money, WXPAY)` 已由回调统一调用记账（用途靠 `orderNo` 末位 `6` 区分）。`sold_count` 仅在此 `+1`，转让链路不动它。回调整体在一个事务里：记账 + 激活 + 自增同生共死。

### 7.3 转让状态机各动作 Service

状态：`10`待付费 / `20`待审核 / `31`已驳回 / `40`待受让人确认 / `51`已拒绝 / `52`已超时 / `60`已撤回 / `70`已生效。`refund_status`：`0`未退 / `1`已退。

#### 7.3.1 发起转让 apply（`POST /api/vipTransfer/apply`）

```java
R apply(Long fromUserId, Long benefitId, Long toUserId, HttpServletRequest req) throws Exception;
```

```
apply(fromUserId, benefitId, toUserId):
    vb = vipBenefitMapper.selectById(benefitId)
    // 前置校验(任一不过抛对应 ERROR_VIP_*)
    校验 vb != null && vb.userId == fromUserId              // ERROR_VIP_NOT_OWNER
    校验 vb.status == 0(正常)                                // ERROR_VIP_BENEFIT_INVALID
    校验 vb.transferable == 1                                // ERROR_VIP_NOT_TRANSFERABLE
    校验 vb.expireTime > now()                               // ERROR_VIP_BENEFIT_EXPIRED
    校验 toUser 存在 且 auditStatus != 2 且 不在 member_blacklist(status=1)  // ERROR_VIP_TO_USER_INVALID
    校验 fromUser 不在黑名单 / 无欠费 / 无退卡违规             // ERROR_VIP_FROM_USER_BLOCKED
    校验 门店等级 fromStore.store_level >= toStore.store_level // ERROR_VIP_STORE_LEVEL
    // 算费:本次=该权益第 (transfer_count+1) 次转让,fee_rule.tiers 取 fromCount<=本次的最大档 fee,无匹配=0
    fee = calcTransferFee(vb)        // 见 7.3.2
    t = new VipBenefitTransfer()
    t.vipBenefitId = benefitId; t.fromUserId = fromUserId; t.toUserId = toUserId
    t.fromStoreId = vb.storeId; t.toStoreId = toUser 归属门店
    t.serviceFee = fee; t.refundStatus = 0
    if fee.compareTo(ZERO) > 0:
        feeOrderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.VIP_TRANSFER_FEE_TYPE
        t.serviceFeeOrderNo = feeOrderNo            // uk_fee_order 唯一
        t.status = 10                               // 待付费
        vipBenefitTransferMapper.insertSelective(t)
        SortedMap params = payService.wxRechargePay(fee, feeOrderNo, null)
        return R.reOk(params)                       // 前端拉起微信付费
    else:
        t.serviceFeeOrderNo = null
        t.status = 20                               // fee=0 直接进待审核
        vipBenefitTransferMapper.insertSelective(t)
        return R.reOk(transferId)
```

> 不在 `apply` 里冻结/改 `vip_benefit`，权益归属直到 §7.4 过户才变更，避免发起阶段就影响持有人使用。

#### 7.3.2 转让费用分档计算

```java
BigDecimal calcTransferFee(VipBenefit vb);   // tiers 来自 vip_benefit_card.fee_rule_id -> vip_fee_rule.tiers_json
```

```
calcTransferFee(vb):
    times = vb.transferCount + 1                  // 本次是第几次转让
    rule = vipFeeRuleMapper.selectByCard(vb.vipCardId)
    if rule == null || rule.status != 1: return ZERO
    tiers = JSON.parse(rule.tiersJson)            // [{fromCount,fee},...] 按 fromCount 升序
    fee = ZERO
    for tier in tiers (升序):
        if tier.fromCount <= times: fee = tier.fee
        else break
    return fee                                    // 无匹配档则 0
```

#### 7.3.3 转让费用微信回调（PayController 同一处再加 else-if）

```java
} else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_TRANSFER_FEE_TYPE)) {
    log.info("-------VIP转让服务费支付成功=========================================");
    vipBenefitTransferService.payFeeCallback(orderNo, wallet, transaction_id, payType);
}
```

```java
int payFeeCallback(String feeOrderNo, BigDecimal money, String transactionNumber, Integer payType);
```

```
payFeeCallback(feeOrderNo, money, txNo, payType):
    // 幂等:仅 status=10 -> 20,顺带留痕微信交易号;重复回调命中 0 行直接返回
    rows = vipBenefitTransferMapper.feePaid(feeOrderNo, txNo)
        // UPDATE vip_benefit_transfer SET status=20, transaction_number=#{txNo}
        // WHERE service_fee_order_no=#{feeOrderNo} AND status=10
    return rows
```

> 服务费记账由回调统一的 `saveIncomePayDetail(orderNo, txNo, money, WXPAY)` 完成（用途靠末位 `7` 区分;付费人=转让人）。付费成功不改 `vip_benefit`。

#### 7.3.4 后台审核 audit（`POST /sys/vipTransfer/audit`，sys 模块）

```java
// SysVipTransferServiceImpl,返回 R.ok()/R.error();@RequiresPermissions("sys:vipTransfer:audit")
R audit(Long transferId, Long auditUserId, boolean pass, String remark);
```

```
audit(transferId, auditUserId, pass, remark):
    if pass:
        rows = mapper.auditPass(transferId, auditUserId, now, remark)
            // UPDATE ... SET status=40, audit_user_id, audit_time, audit_remark,
            //   confirm_deadline = now + N天(可配,默认参 confirm 超时) WHERE transfer_id=? AND status=20
        if rows == 0: return R.error("状态已变更,审核失败")
        push 受让人「有转让待你确认」+ 站内信
    else:
        rows = mapper.auditReject(transferId, auditUserId, now, remark)
            // UPDATE ... SET status=31, audit_user_id, audit_time, audit_remark
            //   WHERE transfer_id=? AND status=20
        if rows == 0: return R.error("状态已变更")
        t = mapper.selectById(transferId)
        doRefund(t)                       // 驳回全额退服务费(见 7.5)
        push 转让人「转让被驳回,服务费已退」
    return R.ok()
```

> sys 的 Mapper XML 热刷新,无需重启;api 的需重启 Tomcat。

#### 7.3.5 受让人确认 confirm（过户入口，`POST /api/vipTransfer/confirm`）

受让人不付费;确认即触发 §7.4 过户单事务。

```java
R confirm(Long toUserId, Long transferId, HttpServletRequest req);
```

```
confirm(toUserId, transferId):
    t = vipBenefitTransferMapper.selectById(transferId)
    校验 t != null && t.toUserId == toUserId && t.status == 40   // ERROR_VIP_TRANSFER_STATE
    校验 t.confirm_deadline >= now()                              // 过期交给定时任务置 52,这里拦截
    transferEffect(transferId)            // 单事务过户,见 7.4
    return R.reOk()
```

#### 7.3.6 受让人拒绝 reject

```java
R reject(Long toUserId, Long transferId);
```

```
reject(toUserId, transferId):
    rows = mapper.toReject(transferId, toUserId)
        // UPDATE ... SET status=51 WHERE transfer_id=? AND to_user_id=? AND status=40
    if rows == 0: return R.reError(ERROR_VIP_TRANSFER_STATE)
    doRefund(mapper.selectById(transferId))    // 拒绝全额退服务费
    push 转让人「受让人已拒绝,服务费已退」
    return R.reOk()
```

#### 7.3.7 转让人撤回 withdraw（20 退费撤回 / 40 不退撤回）

```java
R withdraw(Long fromUserId, Long transferId);
```

```
withdraw(fromUserId, transferId):
    t = mapper.selectByIdForUpdate(transferId)        // 行锁防与回调/审核竞态
    校验 t.fromUserId == fromUserId
    if t.status == 20:                                 // 待审核阶段撤回 -> 退费
        rows = mapper.withdraw20(transferId)           // SET status=60 WHERE id=? AND status=20
        if rows == 0: return R.reError(ERROR_VIP_TRANSFER_STATE)
        doRefund(t)                                    // 20 时撤回退费
    elif t.status == 40:                               // 已审核通过后撤回 -> 不退(R-11/D)
        rows = mapper.withdraw40(transferId)           // SET status=60 WHERE id=? AND status=40
        if rows == 0: return R.reError(ERROR_VIP_TRANSFER_STATE)
        // 不退费:refund_status 保持 0
    else:
        return R.reError(ERROR_VIP_TRANSFER_STATE)
    push 受让人「该转让已被撤回」
    return R.reOk()
```

### 7.4 过户单事务 transferEffect

整笔过户在一个方法（一个事务，`api.service.impl` 自动 `REQUIRED`）内完成：行锁 → 条件改归属 → 幂等推进状态机 → 记账 → 推送。任一步异常整笔回滚。

```java
void transferEffect(Long transferId);   // 由 confirm 调用,同事务
```

```
transferEffect(transferId):
    t = vipBenefitTransferMapper.selectByIdForUpdate(transferId)   // 锁转让单
    if t.status != 40: return                                       // 幂等:非待确认直接返回
    vb = vipBenefitMapper.selectByIdForUpdate(t.vipBenefitId)       // FOR UPDATE 锁权益行,防并发双改
    校验 vb.status == 0                                              // 仍为正常态才可过户
    // 1) 改归属:换持有人 + transfer_count+1 + transferable 维持 1(受让方可再链式转让);
    //    expire_time 不变(继承剩余有效期,到期日不重置 D-10);origin_user_id 不动(留痕)
    rows1 = vipBenefitMapper.changeOwner(vb.vipBenefitId, t.toUserId, t.toStoreId)
        // UPDATE vip_benefit SET user_id=#{toUserId}, store_id=#{toStoreId},
        //   transfer_count = transfer_count + 1, transferable = 1
        // WHERE vip_benefit_id=#{id} AND user_id=#{t.fromUserId} AND status=0
    if rows1 == 0: return                                           // 并发已处理,幂等退出
    // 2) 幂等推进状态机:仅 40 -> 70
    rows2 = vipBenefitTransferMapper.effect(transferId, now)
        // UPDATE vip_benefit_transfer SET status=70, effect_time=#{now}
        // WHERE transfer_id=#{id} AND status=40
    if rows2 == 0: return                                           // 双保险
    // 3) 记账:转让人缴的服务费收入,anotherId 记对方(受让人)userId
    if t.serviceFee > 0:
        vipIncomeRecord(t.serviceFeeOrderNo, t.transactionNumber, t.serviceFee,
                        ConfigConstant.WXPAY, t.fromUserId, t.toUserId)
        // 复用 income_pay_detail:userId=转让人,anotherId=受让人,用途靠 orderNo 末位 7;
        // 若服务费回调时已 saveIncomePayDetail,则此处仅补 anotherId(update),避免重复入账
    // 4) 推送双方(站内信 systemMsg + 友盟 UMengPush)
    push(t.fromUserId, "转让完成,权益已转出")
    push(t.toUserId,   "转让完成,你已获得权益")
```

> 私教课时 / 储值 / 运动档案天然不动（不在事务触及的表内）。两把 `FOR UPDATE`（转让单 + 权益）+ 三处「带状态条件 UPDATE」共同保证：重复确认、并发确认、回调与确认交错均只生效一次。

### 7.5 退费封装 doRefund

复用 `PayService.wxRefund`，按其入参约定组 `params`（`orderNo` 用原微信支付单号、`realPayment` 为 `BigDecimal` 元，内部自乘 100 转分）；成功后幂等置 `refund_status=1`。

```java
void doRefund(VipBenefitTransfer t) throws Exception;
```

```
doRefund(t):
    if t.refundStatus == 1: return                  // 已退,幂等
    if t.serviceFee == null || t.serviceFee <= 0: return
    Map params = new HashMap()
    params.put("orderNo", t.serviceFeeOrderNo)      // 即微信 out_trade_no/out_refund_no
    params.put("realPayment", t.serviceFee)         // BigDecimal 元
    String res = payService.wxRefund(params)        // 返回非 null 即微信退款受理成功
    if res == null: throw new RRException(ERROR_VIP_REFUND_FAIL)   // 抛出 -> 整事务回滚,不置已退
    rows = vipBenefitTransferMapper.markRefunded(t.transferId)
        // UPDATE vip_benefit_transfer SET refund_status=1
        // WHERE transfer_id=#{id} AND refund_status=0
    // rows==0 表示已被并发置过,幂等忽略
```

> 退费在驳回/拒绝/超时/20撤回场景调用;40撤回不调（不退）。退款记账可视需要再写一条 `income_pay_detail`（负向）按现有退款范式处理。

### 7.6 超时定时任务 scanConfirmTimeout

挂到 `api.schedule`（仿现有每小时 `UpdateOrderStatusTask` 节奏），扫 `status=40` 且 `confirm_deadline < now` 的转让单，逐笔置 `52` 已超时 + 退费 + 推送。要在双方都没打开 App 时也能自动退款，故必须定时任务。

```java
// VipTransferTimeoutTask(api.schedule),@Scheduled 或 spring-mvc.xml task 注册
void scanConfirmTimeout();
```

```
scanConfirmTimeout():
    list = vipBenefitTransferMapper.selectTimeout(now)
        // SELECT * FROM vip_benefit_transfer WHERE status=40 AND confirm_deadline < #{now}
    for t in list:
        try:
            // 幂等推进:仅 40 -> 52,命中 0 行说明已被确认/撤回,跳过
            rows = vipBenefitTransferMapper.timeout(t.transferId)
                // UPDATE ... SET status=52 WHERE transfer_id=? AND status=40
            if rows == 0: continue
            t2 = mapper.selectById(t.transferId)
            doRefund(t2)                              // 超时全额退服务费
            push(t.fromUserId, "受让人超时未确认,转让已关闭,服务费已退")
            push(t.toUserId,   "确认已超时")
        catch e:
            log.error("VIP转让超时处理失败 transferId=" + t.transferId, e)
            continue                                  // 单笔失败不影响其余,下轮再扫
```

> 每笔独立事务（逐笔调 Service 方法,使每笔 `40->52 + 退费` 各为一个 `REQUIRED` 事务），一笔退款失败不阻塞其余;`40->52` 的状态条件保证与受让人确认/撤回的竞态下不会重复退款。

### 7.7 幂等 / 事务边界 / 并发 小结

- **幂等**：所有推进动作均为「带源状态条件的 `UPDATE ... WHERE status=<期望源态>`」，靠受影响行数 `>0` 判定是否首次执行;`sold_count+1`、过户改归属、退费置位皆然。微信回调天然可能重投，激活/付费回调均能安全重入。
- **事务边界**：`activateByOrderNo`、`payFeeCallback`、`transferEffect`、`audit`、各 `withdraw/reject/confirm`、`doRefund` 均在 `api.service.impl` / `sys.service.impl` 内,命中切面 `REQUIRED`,单方法即单事务,内部多表改动同生共死。过户（改 `vip_benefit` + 推进 `vip_benefit_transfer` + 记账）严格在 `transferEffect` 一个事务内。
- **并发**：被转让的 `vip_benefit` 行与对应 `vip_benefit_transfer` 行在过户/撤回时一律 `SELECT ... FOR UPDATE` 加行锁,串行化「确认 vs 撤回 vs 超时」三方竞争,再叠加状态条件 UPDATE,杜绝重复过户与重复退费。
## 8. 前置校验规则集与计算实现

本章给出三段可直接落地的实现：转让前置校验集 `checkTransferable`、动态购买价计算 `calcCurrentPrice`、转让费用分档 `calcTransferFee`。三者均放在 `com.dlc.modules.api.service.impl` 下的转让/购买 Service 实现里（自动 REQUIRED 事务）。校验/计算命中失败时统一抛 `RRException(CodeAndMsg.ERROR_VIP_xxx)`，由 `RRExceptionHandler` 兜底转成 `R.reError`（code 取 `CodeAndMsg` 值）。

### 8.1 本章新增 CodeAndMsg 码

> 从 -50 起，避开现有 -1~-36；与其他章节合并到同一枚举时按下表登记，命名为 `ERROR_VIP_*` 风格。本章 `checkTransferable` 实际用到的码如下。

| 枚举名 | code | msg |
|---|---|---|
| `ERROR_VIP_BENEFIT_NOT_EXIST` | -50 | 权益不存在 |
| `ERROR_VIP_BENEFIT_NOT_OWNER` | -51 | 该权益不属于当前用户 |
| `ERROR_VIP_BENEFIT_STATUS_ABNORMAL` | -52 | 权益状态异常，不可转让 |
| `ERROR_VIP_BENEFIT_EXPIRED` | -53 | 权益已过期，不可转让 |
| `ERROR_VIP_BENEFIT_NOT_TRANSFERABLE` | -54 | 该权益已被冻结，不可转让 |
| `ERROR_VIP_FROM_USER_LOCKED` | -55 | 转让人账号已被禁用 |
| `ERROR_VIP_TO_USER_LOCKED` | -56 | 受让人账号已被禁用 |
| `ERROR_VIP_FROM_USER_BLACKLIST` | -57 | 转让人在黑名单，不可转让 |
| `ERROR_VIP_TO_USER_BLACKLIST` | -58 | 受让人在黑名单，不可接收 |
| `ERROR_VIP_FROM_USER_ARREARS` | -59 | 转让人存在欠费，不可转让（口径待对齐） |
| `ERROR_VIP_FROM_USER_CARD_REFUND` | -60 | 转让人已办理退卡，不可转让（口径待对齐） |
| `ERROR_VIP_FROM_USER_VIOLATION` | -61 | 转让人存在重大违规，不可转让（口径待对齐） |
| `ERROR_VIP_TO_USER_NOT_EXIST` | -62 | 受让人不存在 |
| `ERROR_VIP_TRANSFER_TO_SELF` | -63 | 不能转让给自己 |
| `ERROR_VIP_STORE_LEVEL_LIMIT` | -64 | 受让人门店等级高于转让人，不可转让 |
| `ERROR_VIP_CARD_OFF_SHELF` | -65 | 权益卡已下架，不可转让 |

### 8.2 前置校验集 `checkTransferable`

**签名**（校验失败直接抛异常，无返回值即视为通过）：

```java
/**
 * 转让前置校验集：发起(apply)与审核(audit)两处都必须调用同一份。
 * @param benefit   被转让的权益实例(vip_benefit)，调用方按 vip_benefit_id 加锁查出后传入
 * @param fromUser  转让人(user_info)
 * @param toUser    受让人(user_info)，可能为 null(受让人不存在)
 * @param fromStore 转让人门店(store)，用于等级校验
 * @param toStore   受让人门店(store)，用于等级校验
 * 任一条命中即 throw new RRException(对应 CodeAndMsg)。
 */
void checkTransferable(VipBenefit benefit, UserInfo fromUser, UserInfo toUser,
                       Store fromStore, Store toStore);
```

**为什么 apply 和 audit 都要跑**：发起到审核之间有时间差（R-12 门店 1–2 工作日审核），其间权益可能过期、转让人/受让人可能被拉黑或封禁、权益卡可能下架、受让人门店等级可能变动。审核时必须用**审核当下**的数据重跑全部规则，命中即驳回（status→31 已驳回并触发退费，R-11）。apply 命中则直接拦截，转让单不创建、服务费支付单不生成。

**逐条规则**（顺序即执行顺序，先查存在性与归属，再查状态，再查人，最后查门店）：

| 序号 | 规则（命中条件=拦截） | 对应需求 | 命中抛 |
|---|---|---|---|
| 1 | `benefit == null`（权益不存在） | R-08 | `ERROR_VIP_BENEFIT_NOT_EXIST` |
| 2 | `!benefit.getUserId().equals(fromUser.getUserId())`（权益不属于转让人，链式转让后持有人已变更，须以当前 `user_id` 为准） | R-08 | `ERROR_VIP_BENEFIT_NOT_OWNER` |
| 3 | `benefit.getStatus() != 0`（非「0 正常」：9 待支付 / 1 已转出失效 / 2 已冻结 / 3 已过期 一律拦截） | R-08/R-17 | `ERROR_VIP_BENEFIT_STATUS_ABNORMAL` |
| 4 | `benefit.getExpireTime() == null || !benefit.getExpireTime().after(now)`（到期日 ≤ 当前时间，即已过期或无到期日；实时判断，不依赖定时任务，R-03） | R-08/R-17 | `ERROR_VIP_BENEFIT_EXPIRED` |
| 5 | `benefit.getTransferable() != 1`（被冻结/拉黑/违规时置 0；注意受让所得权益默认仍为 1，可链式再转） | R-16/R-17 | `ERROR_VIP_BENEFIT_NOT_TRANSFERABLE` |
| 6 | `fromUser.getAuditStatus() != null && fromUser.getAuditStatus() == 2`（转让人被封禁） | R-04/R-08 | `ERROR_VIP_FROM_USER_LOCKED` |
| 7 | `isInBlacklist(fromUser.getUserId())`（转让人在 `member_blacklist` 中 `status=1` 生效记录） | R-04 | `ERROR_VIP_FROM_USER_BLACKLIST` |
| 8 | `toUser == null`（受让人不存在；按受让人手机号/标识查 `user_info` 为空） | R-12 | `ERROR_VIP_TO_USER_NOT_EXIST` |
| 9 | `toUser.getUserId().equals(fromUser.getUserId())`（不能转给自己） | R-10 约束 | `ERROR_VIP_TRANSFER_TO_SELF` |
| 10 | `toUser.getAuditStatus() != null && toUser.getAuditStatus() == 2`（受让人被封禁） | R-04 | `ERROR_VIP_TO_USER_LOCKED` |
| 11 | `isInBlacklist(toUser.getUserId())`（受让人在黑名单） | R-04 | `ERROR_VIP_TO_USER_BLACKLIST` |
| 12 | **欠费**：`hasArrears(fromUser.getUserId())`【口径待对齐占位】— 暂以「该转让人是否存在未付清的卡/订单」为口径，方法体先 `return false`（不拦截），落地前由业务确认 | R-12/R-17 | `ERROR_VIP_FROM_USER_ARREARS` |
| 13 | **是否办理退卡**：`hasCardRefund(fromUser.getUserId())`【占位】— 暂查转让人名下卡是否处于退卡/退款流程，方法体先 `return false`，待对齐 | R-17 | `ERROR_VIP_FROM_USER_CARD_REFUND` |
| 14 | **重大违规**：`hasSeriousViolation(fromUser.getUserId())`【占位】— 违规记录来源未定，方法体先 `return false`，待对齐 | R-17 | `ERROR_VIP_FROM_USER_VIOLATION` |
| 15 | 权益卡未下架：按 `benefit.getVipCardId()` 查 `vip_benefit_card`，`card == null || card.getStatus() != 1`（非「1 上架」即下架）则拦截 | R-02 | `ERROR_VIP_CARD_OFF_SHELF` |
| 16 | 门店等级：`fromStore == null || toStore == null || fromStore.getStoreLevel() < toStore.getStoreLevel()`（要求转让人门店等级 ≥ 受让人门店等级，高级→低级单向） | R-01 | `ERROR_VIP_STORE_LEVEL_LIMIT` |

> 占位项（12/13/14）：当前以独立 `boolean` 私有方法承载，统一 `return false`（即不拦截），并在方法上用注释标明「口径待对齐」。这样既保留拦截位、不阻塞主流程，又便于业务确认后单点补全，无需改 `checkTransferable` 主体。

**Java 实现**：

```java
@Autowired private VipBenefitCardMapper vipBenefitCardMapper;
@Autowired private MemberBlacklistMapper memberBlacklistMapper;

@Override
public void checkTransferable(VipBenefit benefit, UserInfo fromUser, UserInfo toUser,
                              Store fromStore, Store toStore) {
    Date now = new Date();

    // 1. 权益存在
    if (benefit == null) {
        throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_NOT_EXIST.getMsg(),
                CodeAndMsg.ERROR_VIP_BENEFIT_NOT_EXIST.getCode());
    }
    // 2. 归属转让人(以当前持有人 user_id 为准)
    if (!benefit.getUserId().equals(fromUser.getUserId())) {
        throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_NOT_OWNER.getMsg(),
                CodeAndMsg.ERROR_VIP_BENEFIT_NOT_OWNER.getCode());
    }
    // 3. status=0 正常
    if (benefit.getStatus() == null || benefit.getStatus() != 0) {
        throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_STATUS_ABNORMAL.getMsg(),
                CodeAndMsg.ERROR_VIP_BENEFIT_STATUS_ABNORMAL.getCode());
    }
    // 4. expire_time > now (实时判断，R-03)
    if (benefit.getExpireTime() == null || !benefit.getExpireTime().after(now)) {
        throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_EXPIRED.getMsg(),
                CodeAndMsg.ERROR_VIP_BENEFIT_EXPIRED.getCode());
    }
    // 5. transferable=1
    if (benefit.getTransferable() == null || benefit.getTransferable() != 1) {
        throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_NOT_TRANSFERABLE.getMsg(),
                CodeAndMsg.ERROR_VIP_BENEFIT_NOT_TRANSFERABLE.getCode());
    }
    // 6. 转让人未封禁(auditStatus != 2)
    if (fromUser.getAuditStatus() != null && fromUser.getAuditStatus() == 2) {
        throw new RRException(CodeAndMsg.ERROR_VIP_FROM_USER_LOCKED.getMsg(),
                CodeAndMsg.ERROR_VIP_FROM_USER_LOCKED.getCode());
    }
    // 7. 转让人不在黑名单
    if (isInBlacklist(fromUser.getUserId())) {
        throw new RRException(CodeAndMsg.ERROR_VIP_FROM_USER_BLACKLIST.getMsg(),
                CodeAndMsg.ERROR_VIP_FROM_USER_BLACKLIST.getCode());
    }
    // 8. 受让人存在
    if (toUser == null) {
        throw new RRException(CodeAndMsg.ERROR_VIP_TO_USER_NOT_EXIST.getMsg(),
                CodeAndMsg.ERROR_VIP_TO_USER_NOT_EXIST.getCode());
    }
    // 9. 不能转给自己
    if (toUser.getUserId().equals(fromUser.getUserId())) {
        throw new RRException(CodeAndMsg.ERROR_VIP_TRANSFER_TO_SELF.getMsg(),
                CodeAndMsg.ERROR_VIP_TRANSFER_TO_SELF.getCode());
    }
    // 10. 受让人未封禁
    if (toUser.getAuditStatus() != null && toUser.getAuditStatus() == 2) {
        throw new RRException(CodeAndMsg.ERROR_VIP_TO_USER_LOCKED.getMsg(),
                CodeAndMsg.ERROR_VIP_TO_USER_LOCKED.getCode());
    }
    // 11. 受让人不在黑名单
    if (isInBlacklist(toUser.getUserId())) {
        throw new RRException(CodeAndMsg.ERROR_VIP_TO_USER_BLACKLIST.getMsg(),
                CodeAndMsg.ERROR_VIP_TO_USER_BLACKLIST.getCode());
    }
    // 12. 欠费【口径待对齐:占位,默认不拦截】
    if (hasArrears(fromUser.getUserId())) {
        throw new RRException(CodeAndMsg.ERROR_VIP_FROM_USER_ARREARS.getMsg(),
                CodeAndMsg.ERROR_VIP_FROM_USER_ARREARS.getCode());
    }
    // 13. 是否办理退卡【占位,默认不拦截】
    if (hasCardRefund(fromUser.getUserId())) {
        throw new RRException(CodeAndMsg.ERROR_VIP_FROM_USER_CARD_REFUND.getMsg(),
                CodeAndMsg.ERROR_VIP_FROM_USER_CARD_REFUND.getCode());
    }
    // 14. 重大违规【占位,默认不拦截】
    if (hasSeriousViolation(fromUser.getUserId())) {
        throw new RRException(CodeAndMsg.ERROR_VIP_FROM_USER_VIOLATION.getMsg(),
                CodeAndMsg.ERROR_VIP_FROM_USER_VIOLATION.getCode());
    }
    // 15. 权益卡未下架(status=1 上架)
    VipBenefitCard card = vipBenefitCardMapper.selectById(benefit.getVipCardId());
    if (card == null || card.getStatus() == null || card.getStatus() != 1) {
        throw new RRException(CodeAndMsg.ERROR_VIP_CARD_OFF_SHELF.getMsg(),
                CodeAndMsg.ERROR_VIP_CARD_OFF_SHELF.getCode());
    }
    // 16. 门店等级:转让人门店等级 >= 受让人门店等级 (R-01)
    if (fromStore == null || toStore == null
            || fromStore.getStoreLevel() == null || toStore.getStoreLevel() == null
            || fromStore.getStoreLevel() < toStore.getStoreLevel()) {
        throw new RRException(CodeAndMsg.ERROR_VIP_STORE_LEVEL_LIMIT.getMsg(),
                CodeAndMsg.ERROR_VIP_STORE_LEVEL_LIMIT.getCode());
    }
}

/** 在 member_blacklist 中存在 status=1(生效) 的记录即视为黑名单 */
private boolean isInBlacklist(Long userId) {
    Integer cnt = memberBlacklistMapper.countActiveByUserId(userId); // WHERE user_id=? AND status=1
    return cnt != null && cnt > 0;
}

// ===== 以下三项口径待对齐,占位先一律不拦截,待业务确认后实现 =====
/** 欠费判定【口径待对齐】 */
private boolean hasArrears(Long userId) { return false; }
/** 是否办理退卡【口径待对齐】 */
private boolean hasCardRefund(Long userId) { return false; }
/** 重大违规【口径待对齐】 */
private boolean hasSeriousViolation(Long userId) { return false; }
```

> 并发提示：apply 与 audit 调用前，调用方应先按 `vip_benefit_id` 行级锁查出 `benefit`（`SELECT ... FOR UPDATE`），再进 `checkTransferable`，避免与并发的链式转让/购买回调读到旧持有人或旧状态。

### 8.3 动态购买价计算 `calcCurrentPrice`

按契约公式：`n=sold_count`；若 `step_num<=0` 或 `step_add_price<=0` 返回 `price`；若 `n<=base_buy_count` 返回 `price`；否则 `tier=floor((n-base_buy_count)/step_num)`，`p=price+tier*step_add_price`，`price_cap` 非空时 `p=min(p,price_cap)`。下单时后端按当前 `sold_count` 重算，不信前端；`sold_count` 仅在购买支付成功回调 +1（转让不 +）。

```java
/**
 * 动态购买价:按权益卡当前真实累计购买人数 sold_count 实时计算。
 * 下单时后端用本方法重算金额,不信前端传入的价格。
 */
public BigDecimal calcCurrentPrice(VipBenefitCard card) {
    BigDecimal price = card.getPrice();                 // 基础售价
    Integer stepNum = card.getStepNum();                // 每多少人涨一档
    BigDecimal stepAddPrice = card.getStepAddPrice();   // 每档加价
    Integer baseBuyCount = card.getBaseBuyCount();      // 起涨基数
    Integer soldCount = card.getSoldCount();            // 真实累计购买人数

    // 关闭动态涨价:step_num<=0 或 step_add_price<=0
    if (stepNum == null || stepNum <= 0
            || stepAddPrice == null || stepAddPrice.compareTo(BigDecimal.ZERO) <= 0) {
        return price;
    }
    int n = (soldCount == null) ? 0 : soldCount;
    int base = (baseBuyCount == null) ? 0 : baseBuyCount;
    // 已购 <= 起涨基数:不涨价
    if (n <= base) {
        return price;
    }
    // tier = floor((n - base) / step_num)  (整数除法即向下取整)
    int tier = (n - base) / stepNum;
    BigDecimal p = price.add(stepAddPrice.multiply(new BigDecimal(tier)));
    // 封顶:price_cap 非空时取 min
    BigDecimal cap = card.getPriceCap();
    if (cap != null && p.compareTo(cap) > 0) {
        p = cap;
    }
    return p;
}
```

**边界**：`step_num<=0` 或 `step_add_price<=0` → 关闭涨价返回基础价；`sold_count<=base_buy_count` → 返回基础价；`price_cap=NULL` → 不封顶。整数除法即向下取整，无需额外 `Math.floor`。`tier` 在 `n>base` 且 `n-base<step_num` 时为 0，正好返回基础价，与「刚越过基数尚未满一档不涨」一致。

### 8.4 转让费用分档 `calcTransferFee`

本次为该权益第 `transfer_count+1` 次转让；在 `fee_rule.tiers`（按 `fromCount` 升序）中取 `fromCount<=本次` 的最大档 `fee`，无匹配=0。`tiers_json` 形如 `[{"fromCount":1,"fee":0},{"fromCount":2,"fee":50},{"fromCount":3,"fee":100}]`，用 FastJSON 解析（项目既有 `JSON.parseObject` / `JSONArray.parseArray` 范式）。

```java
/**
 * 计算本次转让费用(转让人缴,D-3/D-4)。
 * @param benefit 被转让权益,取其 transfer_count
 * @param feeRule 关联的 vip_fee_rule(可能为 null=权益卡未配规则=免费);其 tiers_json 为分档配置
 * @return 本次应缴服务费;空规则/无匹配档=0
 */
public BigDecimal calcTransferFee(VipBenefit benefit, VipFeeRule feeRule) {
    // 空规则 或 规则停用(status!=1) 或 tiers 为空 → 免费
    if (feeRule == null || feeRule.getStatus() == null || feeRule.getStatus() != 1) {
        return BigDecimal.ZERO;
    }
    String tiersJson = feeRule.getTiersJson();
    if (tiersJson == null || tiersJson.trim().isEmpty()) {
        return BigDecimal.ZERO;
    }
    // 本次=第 (transfer_count + 1) 次转让
    int currentCount = (benefit.getTransferCount() == null ? 0 : benefit.getTransferCount()) + 1;

    List<FeeTier> tiers = JSONArray.parseArray(tiersJson, FeeTier.class);
    if (tiers == null || tiers.isEmpty()) {
        return BigDecimal.ZERO;
    }
    // 取 fromCount <= currentCount 中 fromCount 最大的那一档的 fee(不依赖入库顺序,遍历求最大命中档)
    BigDecimal fee = BigDecimal.ZERO;
    int matchedFrom = -1;
    for (FeeTier t : tiers) {
        if (t == null || t.getFromCount() == null || t.getFee() == null) {
            continue;
        }
        if (t.getFromCount() <= currentCount && t.getFromCount() > matchedFrom) {
            matchedFrom = t.getFromCount();
            fee = t.getFee();
        }
    }
    return fee; // 无任何 fromCount<=currentCount 的档 → 保持 ZERO
}

/** tiers_json 元素结构,FastJSON 反序列化用 */
public static class FeeTier {
    private Integer fromCount; // 从第几次转让起适用
    private BigDecimal fee;    // 该档定额费用
    public Integer getFromCount() { return fromCount; }
    public void setFromCount(Integer fromCount) { this.fromCount = fromCount; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}
```

**边界**：
- 空规则（`feeRule==null`）/ 规则停用（`status!=1`）/ `tiers_json` 为空或解析为空数组 → 返回 0（免费），对应「第 1 次可设免费」「未配规则即免费」。
- 无匹配档（所有 `fromCount` 都 `> currentCount`，例如规则最小档 `fromCount=2` 而本次是第 1 次）→ 返回 0。
- 取「`fromCount<=currentCount` 的最大档」用遍历求最大命中实现，不依赖 `tiers_json` 入库顺序；最后一档（如 `fromCount=3,fee=100`）天然兜底所有更高次数（第 3、4、5… 次均命中它）。
- 费用与 `vip_benefit.origin_price`、与动态购买价均无关——转让费按次数分档定额，不按购买价比例。
- 解析与币种：`fee` 用 `BigDecimal`，避免浮点误差，金额单位与 `service_fee`(DECIMAL(10,2)) 一致。`JSONArray.parseArray` 解析失败会抛 FastJSON 异常，配置入库前应由后台 `vip_fee_rule` 保存接口校验 `tiers_json` 合法性，运行期不再容错吞异常。

---

**写入文件**：`/Users/macmima0000/.claude/jobs/7a25481a/tmp/vip_td_s5.md`
## 9. 改动文件清单

> 标注规则：🆕新建文件 / 🔧改现有文件。凡 api 侧的 Mapper XML（`mapper/api/*.xml`）改动或新增，**改完必须重启 Tomcat 才生效**（`MybatisMapperRefresh` 只热刷 `mapper/sys/*.xml`，启动时一次性加载全部 `classpath:mapper/**/*.xml`）；sys 侧 Mapper XML 可热刷新。

### 9.1 后端 api 侧（`com.dlc.modules.api`，移动端，`R.reOk/reError`，身份走 `BaseController`）

权益卡购买与展示：

| 类型 | 文件 | 说明 |
|---|---|---|
| 🆕 controller | `VipCardController.java` | `@RequestMapping("/api/vipCard")`：`/list` `/detail` `/buy` `/myBenefits`。`buy` 后端按当前 `sold_count` 重算动态价、不信前端价 |
| 🆕 service | `VipCardService.java` | 接口 |
| 🆕 serviceImpl | `VipCardServiceImpl.java` | 列表/详情（含实时购买人数+动态价计算）、下单建权益卡订单、`updateVipCardOrder(orderNo, money, transactionNumber, payType)` 供回调建 `vip_benefit` 实例+`sold_count+1`（仅购买回调 +1） |
| 🆕 dao | `VipCardDao.java` | 权益卡商品 Mapper 接口 |
| 🆕 entity | `VipBenefitCardEntity.java` | 对应 `vip_benefit_card` |
| 🆕 dao | `VipBenefitDao.java` | 用户持有实例 Mapper 接口 |
| 🆕 entity | `VipBenefitEntity.java` | 对应 `vip_benefit` |
| 🆕 mapperXml | `mapper/api/VipBenefitCardMapper.xml` | 商品查询/动态价字段（**改动需重启**） |
| 🆕 mapperXml | `mapper/api/VipBenefitMapper.xml` | 持有实例查询/过户更新（**改动需重启**） |

权益转让（核心状态机）：

| 类型 | 文件 | 说明 |
|---|---|---|
| 🆕 controller | `VipTransferController.java` | `@RequestMapping("/api/vipTransfer")`：`/quote` `/apply` `/myList` `/withdraw` `/confirm` `/reject` |
| 🆕 service | `VipTransferService.java` | 接口 |
| 🆕 serviceImpl | `VipTransferServiceImpl.java` | 费用试算、发起（前置校验集+建转让单+生成服务费支付单）、`updateTransferFeeOrder(orderNo,...)` 供回调把转让单 `status` 10→20、撤回/拒绝、`confirmTransfer` 过户单事务（命中 api.service.impl 切面 REQUIRED，幂等去重） |
| 🆕 dao | `VipBenefitTransferDao.java` | Mapper 接口 |
| 🆕 entity | `VipBenefitTransferEntity.java` | 对应 `vip_benefit_transfer` |
| 🆕 mapperXml | `mapper/api/VipBenefitTransferMapper.xml` | 转让单查询/状态流转更新、超时扫描查询（**改动需重启**） |
| 🆕 schedule | `com.dlc.modules.api.schedule.VipTransferTimeoutTask.java` | `confirmTimeoutScan()`：扫 `status=40` 且 `confirm_deadline < now` → 置 52 已超时 + `wxRefund` 退服务费 + 推送 |

停卡：

| 类型 | 文件 | 说明 |
|---|---|---|
| 🆕 controller | `CardPauseController.java` | `@RequestMapping("/api/cardPause")`：`/apply` `/list`（恢复可在 list 实时判断，亦可走任务） |
| 🆕 service | `CardPauseService.java` | 接口 |
| 🆕 serviceImpl | `CardPauseServiceImpl.java` | 停卡申请（校验每自然月1次/全年12次）、有效期顺延、恢复 |
| 🆕 dao | `CardPauseRecordDao.java` | Mapper 接口 |
| 🆕 entity | `CardPauseRecordEntity.java` | 对应 `card_pause_record` |
| 🆕 mapperXml | `mapper/api/CardPauseRecordMapper.xml` | 按月/按年计数查询（**改动需重启**） |

🔧 改现有 api 文件：

| 文件 | 改动 |
|---|---|
| `com.dlc.modules.api.controller.PayController` | `wxNotify` 回调按 `orderNo` 末位后缀新增 2 个 `else-if`：命中 `VIP_CARD_BUY_TYPE` → `vipCardService.updateVipCardOrder(...)`；命中 `VIP_TRANSFER_FEE_TYPE` → `vipTransferService.updateTransferFeeOrder(...)`。`rechargePay` 下单入口若走支付宝分支的 `subject` 拼装也补这两个后缀（本功能只接微信，支付宝分支可不补 subject，但回调分支必须补）。注入新增的 `VipCardService`/`VipTransferService` |

> 说明：服务费支付单与权益卡购买单均复用 `PayController.rechargePay` 下单 + `PayService.wxRechargePay(money, orderNo, null)`，回调统一进 `/api/pay/wxNotify`，靠 `orderNo` 末位后缀分发，符合既有范式。

### 9.2 后端 sys 侧（`com.dlc.modules.sys`，后台，`R.ok/error`，`@RequiresPermissions`）

权益卡商品管理（仿 `SysFitCardController` 的 `/list /info/{id} /save /update /delete /onCard /offCard`）：

| 类型 | 文件 | 权限串 |
|---|---|---|
| 🆕 controller | `SysVipCardController.java` `@RequestMapping("sys/vipCard")` | `sys:vipCard:list/info/save/update/delete`（上/下架沿用 update 串） |
| 🆕 service | `SysVipCardService.java` | — |
| 🆕 serviceImpl | `SysVipCardServiceImpl.java` | `sold_count` 后台只读，不接受前端写入 |
| 🆕 dao | `VipCardDao.java`（sys 包内独立 Mapper） | — |
| 🆕 entity | `VipBenefitCardEntity.java`（sys 包内独立 entity） | — |
| 🆕 mapperXml | `mapper/sys/VipCardDao.xml` | 热刷新，无需重启 |

转让费用规则配置：

| 类型 | 文件 | 权限串 |
|---|---|---|
| 🆕 controller | `SysVipFeeRuleController.java` `@RequestMapping("sys/vipFeeRule")` | `sys:vipFeeRule:list/info/save/update/delete` |
| 🆕 service / serviceImpl | `SysVipFeeRuleService.java` / `SysVipFeeRuleServiceImpl.java` | 校验 `tiers_json` 合法（按 `fromCount` 升序） |
| 🆕 dao / entity | `VipFeeRuleDao.java` / `VipFeeRuleEntity.java` | 对应 `vip_fee_rule` |
| 🆕 mapperXml | `mapper/sys/VipFeeRuleDao.xml` | 热刷新 |

转让审核工作台（仿 `SysIncomePayDetailController.updateTradeStatus`）：

| 类型 | 文件 | 权限串 |
|---|---|---|
| 🆕 controller | `SysVipTransferController.java` `@RequestMapping("sys/vipTransfer")` | `sys:vipTransfer:list/info`、`sys:vipTransfer:audit` |
| 🆕 service / serviceImpl | `SysVipTransferService.java` / `SysVipTransferServiceImpl.java` | `audit`：通过→单 20→40 + 推送受让人 + 写 `confirm_deadline`；驳回→单 20→31 + `wxRefund` 退服务费 + 置 `refund_status=1` + 推送 |
| 🆕 dao / entity | `VipTransferDao.java` / `VipBenefitTransferEntity.java`（sys 包） | — |
| 🆕 mapperXml | `mapper/sys/VipTransferDao.xml` | 热刷新 |

停卡记录（后台只读查询）：

| 类型 | 文件 | 权限串 |
|---|---|---|
| 🆕 controller | `SysCardPauseController.java` `@RequestMapping("sys/cardPause")` | `sys:cardPause:list/info` |
| 🆕 service / serviceImpl / dao / entity | `SysCardPauseService(Impl)`、`CardPauseDao`、`CardPauseRecordEntity`（sys 包） | — |
| 🆕 mapperXml | `mapper/sys/CardPauseDao.xml` | 热刷新 |

会员黑名单：

| 类型 | 文件 | 权限串 |
|---|---|---|
| 🆕 controller | `SysMemberBlacklistController.java` `@RequestMapping("sys/memberBlacklist")` | `sys:memberBlacklist:list/info/save/update/delete`（解除走 update） |
| 🆕 service / serviceImpl / dao / entity | `SysMemberBlacklistService(Impl)`、`MemberBlacklistDao`、`MemberBlacklistEntity` | — |
| 🆕 mapperXml | `mapper/sys/MemberBlacklistDao.xml` | 热刷新 |

🔧 改现有 sys 文件（门店等级 D-6）：

| 文件 | 改动 |
|---|---|
| `com.dlc.modules.sys.entity.StoreEntity`（门店实体，对应 `store`） | 加 `private Integer storeLevel;` 字段 + getter/setter |
| `com.dlc.modules.sys.controller.SysStoreController` | `save`/`update` 入参带上 `storeLevel`（沿用现有 `@RequestBody`，实体已有字段即可） |
| `mapper/sys/StoreDao.xml` | `insert`/`update`/查询列加 `store_level`（sys 热刷新，无需重启） |

### 9.3 跨模块公共/配置改动

| 文件 | 改动 |
|---|---|
| 🔧 `com.dlc.common.utils.ConfigConstant` | 新增 2 个订单号末位后缀常量：`public static final String VIP_CARD_BUY_TYPE = "6";`、`public static final String VIP_TRANSFER_FEE_TYPE = "7";`（现有占用：CARD=2/GOODS=3/PRIVATE_CLASS=4/TEAM_CLASS=5，6、7 未占用，**无冲突**） |
| 🔧 `com.dlc.common.utils.CodeAndMsg` | 新增码（现有负码到 -36，从 -50 起避开）。本功能实际用到：`ERROR_VIP_CARD_OFF_SHELF(-50,"权益卡已下架")`、`ERROR_VIP_BENEFIT_NOT_FOUND(-51,"权益不存在或不属于本人")`、`ERROR_VIP_BENEFIT_EXPIRED(-52,"权益已过期，无法转让")`、`ERROR_VIP_BENEFIT_NOT_TRANSFERABLE(-53,"该权益当前不可转让")`、`ERROR_VIP_TRANSFER_SELF(-54,"不能转让给自己")`、`ERROR_VIP_STORE_LEVEL(-55,"受让人门店等级高于转让人，不可转让")`、`ERROR_VIP_USER_BLACKLIST(-56,"账号在黑名单中，不可转让")`、`ERROR_VIP_TRANSFER_STATUS(-57,"转让单状态不允许该操作")`、`ERROR_VIP_TRANSFER_NOT_OWNER(-58,"无权操作该转让单")`、`ERROR_VIP_PAUSE_MONTH_LIMIT(-59,"本月停卡次数已用完")`、`ERROR_VIP_PAUSE_YEAR_LIMIT(-60,"全年停卡次数已用完")` |
| 🔧 `src/main/resources/spring-mvc.xml` | 新增超时定时任务：加 `<bean id="vipTransferTimeoutTask" class="com.dlc.modules.api.schedule.VipTransferTimeoutTask"/>`，并新增一个 `<task:scheduled-tasks>` 块 `<task:scheduled ref="vipTransferTimeoutTask" method="confirmTimeoutScan" cron="0 0 0/1 * * ?"/>`（沿用现有每小时节奏，与 `UpdateOrderStatusTask` 同频率） |

### 9.4 SQL 脚本（`Backend/sql/`，沿用每改动一文件的命名风格）

| 文件 | 内容 |
|---|---|
| 🆕 `Backend/sql/vip_benefit_transfer.sql` | 6 张新表建表 DDL：`vip_fee_rule`、`vip_benefit_card`、`vip_benefit`、`vip_benefit_transfer`、`card_pause_record`、`member_blacklist`（以契约 DDL 为唯一事实来源）。可按需拆为多文件，建议合并为一个变更脚本 |
| 🆕 `Backend/sql/store_store_level.sql` | `ALTER TABLE store ADD COLUMN store_level INT NOT NULL DEFAULT 1 COMMENT '门店等级,越大越高';` |
| 🆕 `Backend/sql/sys_menu_vip.sql` | 后台菜单+权限插入语句（见 10.3） |

---

## 10. admin 前端改动

> 前端项目在 `admin/`，配置式页面（`r-search` / `r-table` / `r-form`），接口集中在 `src/utils/apis.js`，页面放 `src/views/modules/sys/*.vue`。菜单 url 的 `/` 映射到 `views/modules` 下路径（如 `sys/vipCard` → `views/modules/sys/vipCard.vue`），由后端 `sys_menu` 表驱动动态加载。

### 10.1 新建页面（5 个 .vue，对应《Web 后台界面设计文档》各页）

| 文件 | 菜单 url | 对应后端接口 | 页面要点 |
|---|---|---|---|
| `src/views/modules/sys/vipCard.vue` | `sys/vipCard` | `sys/vipCard/*` | 权益卡商品列表 + 新增/编辑表单（名称、描述、售价、有效天数、适用门店、关联费用规则下拉、动态涨价四参 `base_buy_count/step_num/step_add_price/price_cap`、是否展示购买人数）、上/下架按钮；`sold_count` 只读展示不可编辑 |
| `src/views/modules/sys/vipFeeRule.vue` | `sys/vipFeeRule` | `sys/vipFeeRule/*` | 费用规则列表 + 表单；`tiers_json` 用可增删行的分档编辑器（每行 `fromCount` + `fee`），保存时序列化为 JSON 字符串；启用/停用 |
| `src/views/modules/sys/vipTransfer.vue` | `sys/vipTransfer` | `sys/vipTransfer/list`、`sys/vipTransfer/audit` | 转让审核工作台：r-search 按状态/转让人/受让人筛选，r-table 展示状态机各态；详情抽屉含通过/驳回按钮（驳回填 `audit_remark`），仅 `status=20 待审核` 可操作 |
| `src/views/modules/sys/cardPause.vue` | `sys/cardPause` | `sys/cardPause/list` | 停卡记录只读列表（用户、被停卡、所属月、停卡天数、状态、起止时间） |
| `src/views/modules/sys/memberBlacklist.vue` | `sys/memberBlacklist` | `sys/memberBlacklist/*` | 黑名单列表 + 拉黑表单（user_id、原因）+ 解除按钮（status 1→0） |

🔧 改现有 `src/views/modules/sys/store.vue`（门店管理页）：表单加「门店等级 `storeLevel`」输入项，列表列加等级展示。

### 10.2 `src/utils/apis.js` 新增方法清单

按现有封装风格新增（方法名建议，路径必须对齐后端）：

- 权益卡：`vipCardList`、`vipCardInfo`、`vipCardSave`、`vipCardUpdate`、`vipCardDelete`、`vipCardOnCard`、`vipCardOffCard` → `sys/vipCard/{list,info/{id},save,update,delete,onCard,offCard}`
- 费用规则：`vipFeeRuleList`、`vipFeeRuleInfo`、`vipFeeRuleSave`、`vipFeeRuleUpdate`、`vipFeeRuleDelete` → `sys/vipFeeRule/*`
- 转让审核：`vipTransferList`、`vipTransferInfo`、`vipTransferAudit` → `sys/vipTransfer/{list,info/{id},audit}`
- 停卡：`cardPauseList`、`cardPauseInfo` → `sys/cardPause/{list,info/{id}}`
- 黑名单：`memberBlacklistList`、`memberBlacklistInfo`、`memberBlacklistSave`、`memberBlacklistUpdate`、`memberBlacklistDelete` → `sys/memberBlacklist/*`
- 门店：现有 store 接口若已含 update/save，无需新增方法，仅表单字段加 `storeLevel`

### 10.3 后端 `sys_menu` 需新增的菜单 + 权限记录

在父菜单（如「会员/营销」分组）下新增 5 个菜单项（`type=1` 菜单）及其按钮权限（`type=2` 按钮，`perms` 对应 `@RequiresPermissions` 串），写入 `Backend/sql/sys_menu_vip.sql`：

| 菜单名 | url | 子按钮 perms |
|---|---|---|
| VIP权益卡 | `sys/vipCard` | `sys:vipCard:list/info/save/update/delete` |
| 转让费用规则 | `sys/vipFeeRule` | `sys:vipFeeRule:list/info/save/update/delete` |
| 权益转让审核 | `sys/vipTransfer` | `sys:vipTransfer:list/info/audit` |
| 停卡记录 | `sys/cardPause` | `sys:cardPause:list/info` |
| 会员黑名单 | `sys/memberBlacklist` | `sys:memberBlacklist:list/info/save/update/delete` |

> 门店等级复用现有门店菜单，无需新增菜单，仅页面表单加字段。新增菜单后需给对应角色授权（`sys_role_menu`）。

---

## 11. 落地顺序与自测清单

落地原则：先做相互独立、可单独验收的小项（门店等级 / 权益卡购买含人数与动态涨价 / 停卡 / 黑名单），把支付回调链路打通；最后做转让主流程与审核（依赖前面的权益实例与支付回调）。

### 11.1 落地顺序

1. **建表与脚本**：执行 `Backend/sql/` 三个脚本（6 张新表 + `store.store_level` + `sys_menu`）。
2. **公共改动**：`ConfigConstant` 加 2 个后缀常量、`CodeAndMsg` 加码。
3. **门店等级（独立）**：`StoreEntity` + `StoreDao.xml` + `store.vue` 加 `store_level`，后台维护可写可查。
4. **黑名单（独立）**：sys 黑名单 CRUD + 页面。
5. **费用规则（独立）**：sys `vipFeeRule` CRUD + 分档编辑器页面（为转让试算做准备）。
6. **权益卡商品（独立）**：sys `vipCard` CRUD + 上下架 + 页面（`sold_count` 只读）。
7. **权益卡购买 + 回调（依赖 6）**：api `VipCardController.buy` → `PayController.wxNotify` 加 `VIP_CARD_BUY_TYPE` 分支 → 建 `vip_benefit` 实例 + `sold_count+1`。**改了 `mapper/api/*.xml` 与 PayController，需重启 Tomcat。**
8. **停卡（独立，依赖会员卡 card_order）**：api `cardPause` 申请 + 次数限制 + 有效期顺延。
9. **转让主流程（依赖 5/6/7）**：api `vipTransfer` 试算/发起/撤回/确认/拒绝 + `PayController.wxNotify` 加 `VIP_TRANSFER_FEE_TYPE` 分支 + 过户单事务 + 退费。**改 api Mapper/PayController 需重启。**
10. **审核工作台（依赖 9）**：sys `vipTransfer` 审核接口 + 页面。
11. **超时任务**：`spring-mvc.xml` 注册 `VipTransferTimeoutTask`，重启生效。
12. **菜单授权**：给角色授予新菜单权限，admin 重新登录刷新菜单。

### 11.2 可执行自测清单

> 每步给出操作 + 预期，作为验收 checklist。涉及金额的步骤必须看实际数据库行与 `income_pay_detail` 流水，不靠前端提示。

**A. 后台配置**
- [ ] 建表脚本执行成功，6 张新表存在、`store.store_level` 列存在。
- [ ] sys 后台新建 1 条费用规则，`tiers_json` 存成 `[{"fromCount":1,"fee":0},{"fromCount":2,"fee":50},{"fromCount":3,"fee":100}]`，`status=1`。
- [ ] sys 后台新建 1 张权益卡，关联上面的费用规则，配 `price=200, base_buy_count=10, step_num=100, step_add_price=20, price_cap=300`，上架（`status=1`）。`sold_count` 显示 0 且页面不可编辑。
- [ ] 给两个门店设不同 `store_level`（高店=2、低店=1）。

**B. 购买走通微信回调（动态价 + sold_count）**
- [ ] 小程序 `/api/vipCard/list`/`detail` 返回含实时购买人数与当前价；`sold_count<=base_buy_count` 时价=200。
- [ ] `/api/vipCard/buy` 下单 → 微信支付 → `/api/pay/wxNotify` 回调成功：`vip_benefit` 新增 1 行（`status=0 正常`，`user_id`=购买人，`origin_user_id`=购买人，`expire_time=start+validity_days`），`vip_benefit_card.sold_count` +1，`income_pay_detail` 有 1 条 `payType=2` 流水。
- [ ] 把 `sold_count` 造到 `base_buy_count+step_num` 以上，再查详情：价格按 `price+floor((n-base)/step)*step_add` 上涨，且不超过 `price_cap`。

**C. 发起转让 + 付费**
- [ ] `/api/vipTransfer/quote`：第 1 次转让命中 `fromCount=1` 档，`service_fee=0`。
- [ ] `/api/vipTransfer/apply`（A 高店 → B 低店）：建 `vip_benefit_transfer`（`status=10 待付费`，`service_fee` 按档），生成服务费支付单（末位后缀 7）。前置校验：过期/不可转/黑名单/转给自己/门店等级源<目标 均被对应 `ERROR_VIP_*` 拦截。
- [ ] 服务费 >0 时支付 → `/api/pay/wxNotify` 命中 `VIP_TRANSFER_FEE_TYPE` → 转让单 `status` 10→20（待审核），`income_pay_detail` 记 1 条、`anotherId` 记受让人 userId。

**D. 审核**
- [ ] sys `vipTransfer` 列表见到 `status=20` 的待审单；点驳回 → `status=31`，触发 `wxRefund` 全额退、`refund_status=1`，推送双方，权益仍归 A。
- [ ] 另发一单走通过 → `status=40 待受让人确认`，写 `confirm_deadline`，推送 B。

**E. 受让确认过户（核心事务）**
- [ ] B `/api/vipTransfer/confirm`（不付费）→ 单 `status=70 已生效`、`effect_time` 写入；同事务内：`vip_benefit.user_id` 改为 B、`transfer_count+1`、`transferable` 仍=1、`origin_user_id` 不变、`expire_time` 不变（继承剩余有效期，**不重置**）；A 原持有视角该权益失效（持有人已变更）。
- [ ] 幂等校验：重复回调/重复确认不产生第二次过户、不重复 +1。
- [ ] 私教课时/储值/运动档案三表数据未变（仍归 A）。

**F. 链式与各分支退费**
- [ ] B 再发起转让给 C：`quote` 命中第 2 档（`transfer_count=1` → 第 2 次 → `fee=50`），过户后 `transfer_count=2`，`expire_time` 仍不变（越转越少）。
- [ ] 撤回分支：A 付费且审核通过后（`status=40`）转让人 `/withdraw` → `status=60`、**服务费不退**（`refund_status=0`）。
- [ ] 拒绝分支：B `/reject` → `status=51`、`wxRefund` 全额退、`refund_status=1`。
- [ ] 超时分支：把某 `status=40` 单的 `confirm_deadline` 改到过去，跑 `VipTransferTimeoutTask.confirmTimeoutScan` → `status=52`、退款、推送。

**G. 停卡限制**
- [ ] 同一年卡同一自然月第 2 次停卡被 `ERROR_VIP_PAUSE_MONTH_LIMIT` 拦截；全年第 13 次被 `ERROR_VIP_PAUSE_YEAR_LIMIT` 拦截。
- [ ] 停卡后 `card_pause_record` 写入（`status=0 停卡中`、`pause_month=当前 yyyy-MM`），恢复后有效期按 `pause_days` 顺延。

**H. 动态涨价收尾校验**
- [ ] `sold_count` 仅在购买回调 +1，转让确认过户**不 +1**（核对 E/F 后 `sold_count` 未因转让变化）。
- [ ] `step_num<=0` 或 `step_add_price<=0` 的权益卡始终返回基础 `price`，不动态涨价。
## 12. 设计评审记录

> 评审对象：第 1–11 章（vip_td_s1~s6）。已对照共享契约 + 真实代码库（`ConfigConstant`、`CodeAndMsg`、`PayController.rechargeCallBack`、`IncomePayDetailServiceImpl.saveIncomePayDetail`、`PayServiceImpl.wxRefund`、`spring-jdbc.xml` 事务切面、`OrderNoGenerator`）逐条核验。结论：**整体方案可行、状态机闭环正确、幂等/锁设计基本到位，但存在若干会直接导致线上 bug 或脏数据的硬伤，必须在落地前修正。**

---

### 12.1 高危问题（H，会导致功能不成立/脏数据/资金风险）

#### H-1 `saveIncomePayDetail` 实际签名不支持记 `anotherId`/对方 userId，且新后缀 6/7 落不进 userId/storeId
- **位置**：s1 §4 记账行、s2 §5.9、s4 §7.2.3/§7.3.3/§7.4(step3)。
- **事实（已核验）**：真实方法 `IncomePayDetailServiceImpl.saveIncomePayDetail(String orderNo, String transaction_id, BigDecimal wallet, Integer payType)` **没有 anotherId 参数**；内部对 `userId`/`storeId` 的赋值是写死的 `if/else`，只认后缀 `2/3/4/5`（CARD/PRIVATE_CLASS/TEAM_CLASS/GOODS）。后缀 `6`(权益卡购买)/`7`(转让服务费) 会全部落空 → `income_pay_detail.userId=NULL、storeId=NULL`，且无法写 `anotherId`。
- **影响**：契约和多章反复声称「`anotherId` 记对方 userId」用现有方法**根本做不到**；权益卡/转让流水会缺 userId/storeId，财务报表对账断裂。
- **修复**：必须改 `saveIncomePayDetail`：① 增加后缀 `6`/`7` 两个 else-if 分支，按 `source_order_no`→`vip_benefit` / `service_fee_order_no`→`vip_benefit_transfer` 反查 userId、storeId；② 若需 `anotherId`，要么重载一个带 `anotherId` 的方法，要么转让生效时单独 `update income_pay_detail set another_id=? where order_no=?`。本条必须写进「改现有 api 文件」清单（s6 §9.1 漏列了对 `IncomePayDetailServiceImpl` 的改造）。

#### H-2 回调「记账 + 激活 + sold_count+1 单事务」不成立 —— PayController 不在事务切面内
- **位置**：s4 §7.2.3 收尾「回调整体在一个事务里:记账 + 激活 + 自增同生共死」、§7.4 同类表述、§7.7「事务边界」。
- **事实（已核验）**：事务切面 `txPointcut` 只匹配 `com.dlc.modules.api.service.impl.*.*` 与 `sys.service.impl.*.*`；`PayController.rechargeCallBack` 是 **controller 方法，不被事务覆盖**。现有代码里 `saveIncomePayDetail(...)` 与各 `updateXxxOrder(...)` 是**两次独立事务**，回调方法本身无事务边界。
- **影响**：若 `activateByOrderNo` 抛异常，`saveIncomePayDetail` 已提交的流水不会回滚 → 「有流水无权益」脏数据。设计自述的「同生共死」是错的，落地者会误以为有原子性。
- **修复**：① 把「记账 + 激活 + sold_count+1」封装进**一个** service.impl 方法（如 `vipBenefitService.activateByOrderNo` 内部自己调 `saveIncomePayDetail` 再激活再自增），由 controller 只调这一个方法，才有单事务；或 ② 接受最终一致 + 幂等补偿，但要在文档显式声明「记账与激活非原子，靠幂等重入收敛」，不要写「同生共死」。转让回调 `payFeeCallback`、过户 `transferEffect` 同理需自查是否真的把记账纳入同一 service 方法。

#### H-3 `payType` 与 `tradeType` 混淆，记账的 `payType` 会被写成 6/7 而非渠道
- **位置**：契约「payType 取值对齐现有实现(WXPAY=2)」、s1 §4、s2 §5.9、s4 多处「payType 取 WXPAY=2」。
- **事实（已核验）**：`saveIncomePayDetail` 内 `ipd.setPayType(Integer.valueOf(orderNo.substring(len-1)))` —— `income_pay_detail.payType` 存的是**支付用途=订单后缀**（购买卡=2、私教=4…），渠道存在 `tradeType`（`ipd.setTradeType(payType)`，回调传入的 `WXPAY=2`）。两者字段名在现有实现里**反着用**。
- **影响**：新后缀 6/7 会让 `payType` 字段出现 `6`/`7` 两个新「用途」值——这本身没问题，但各章把「payType=WXPAY=2」当成传给 `saveIncomePayDetail` 的 payType 含义来描述，会误导落地者把渠道写进用途列、或在统计时按 2 过滤而漏掉权益卡流水。
- **修复**：在文档明确：传入 `saveIncomePayDetail` 的第 4 参 `payType` 实为「渠道 WXPAY=2」，落到 `tradeType`；而 `income_pay_detail.payType`（用途）由方法内部按后缀自动取 6/7。统计/对账口径据此更新（新增「6=权益卡购买、7=转让服务费」两个用途枚举）。

#### H-4 CodeAndMsg 码值在 s1/s2/s5/s6 四章互相冲突，无法直接合并
- **位置**：s1 §3.4、s2 §5.0、s5 §8.1、s6 §6.7、s6 §9.3。
- **事实**：同一语义在各章码值/命名不一致，举例（均自称「从 -50 起」但分配冲突）：
  - 「权益不存在」：s1 `ERROR_VIP_BENEFIT_NOT_EXIST=-52`，s2 `=-51`，s5 `=-50`。
  - 「不能转给自己」：s1 `-58`，s2 `-57`，s5 `ERROR_VIP_TRANSFER_TO_SELF=-63`，s6 `ERROR_VIP_TRANSFER_SELF=-54`。
  - 「门店等级」：s1 `ERROR_VIP_STORE_LEVEL_LOW=-60`，s2 `ERROR_VIP_STORE_LEVEL_LIMIT=-58`，s5 `=-64`，s6 `ERROR_VIP_STORE_LEVEL=-54`。
  - s6 §6.7 把 `-50` 复用给 `ERROR_VIP_TRANSFER_NOT_AUDIT`、`-52` 给 `ERROR_VIP_BENEFIT_EXPIRED`，与 s1/s2/s5 全部错位。
  - 命名风格也不统一：`NOT_EXIST` vs `NOT_FOUND`、`STATUS_ABNORMAL` vs `NOT_NORMAL`、`NOT_TRANSFERABLE`（s5 含义是「已冻结」而非「该卡不可转」）。
- **影响**：六章合并进同一个 `CodeAndMsg` 枚举时码值打架，前端按码做文案/分支会错乱；这是「各章节状态码互相一致」这一硬指标的直接违反。
- **修复**：**新增一节「附录 A：CodeAndMsg 统一码表」作为唯一事实来源**，一次性把全部 VIP 码定死（建议 -50 起连续编号，给出 枚举名→码→文案 唯一映射），各章只引用不再各自定义。务必同时统一命名（`NOT_EXIST`、`NOT_OWNER`、`STATUS_ABNORMAL`、`EXPIRED`、`NOT_TRANSFERABLE`、`TO_USER_SELF`、`STORE_LEVEL_LIMIT`、`TRANSFER_STATUS`…）。

#### H-5 「转让进行中」期间权益未占用，可被并发发起第二笔转让 / 被自己再次发起 / 链式叠加
- **位置**：s4 §7.3.1 收尾「不在 apply 里冻结/改 vip_benefit … 避免发起阶段就影响持有人使用」；s2 §5.6。
- **问题**：apply 不锁、不置占用标记。则同一 `vip_benefit` 可同时存在**多笔 status∈{10,20,40} 的在途转让单**。审核通过两笔后，受让人甲先 confirm 过户（`user_id`→甲），受让人乙再 confirm：过户 SQL `WHERE user_id=原from AND status=0` 命中 0 行会幂等退出——**归属安全**，但乙那笔的**服务费已收却不退**（confirm 走的是过户分支，不是退费分支，rows1==0 直接 `return` 静默吞掉），且乙端提示「确认成功」实际没拿到权益 → 资金 + 体验双重事故。
- **影响**：重复发起不是边界料想，是 D-9 链式转让下的常见并发；当前设计只保证不重复过户，不保证「失败的那笔退费 + 正确报错」。
- **修复**：二选一并写进文档——① apply 时对该 `vip_benefit` 加「在途转让」唯一约束（如对 `vip_benefit_transfer` 加 `UNIQUE(vip_benefit_id, status in 在途)` 的等价校验：apply 前 `select ... where vip_benefit_id=? and status in (10,20,40) for update`，存在则报 `ERROR_VIP_TRANSFER_IN_PROGRESS`）；或 ② `transferEffect` 中 `rows1==0` 时不要静默 return，要区分「已被本单处理(幂等)」与「被他单抢走(需对本单退费+置失败态)」。强烈建议采用 ①（最干净），并新增一个码与一个状态语义。

#### H-6 H-2/H-1 连带：转让生效记账「补 anotherId(update)」与「服务费回调已记账」存在重复入账风险且无幂等键
- **位置**：s4 §7.4 step3 注释「若服务费回调时已 saveIncomePayDetail,则此处仅补 anotherId(update)」。
- **问题**：服务费在 `payFeeCallback`（§7.3.3）时已由回调统一 `saveIncomePayDetail` 入账一次；§7.4 又说「记账」，靠注释提醒「仅补 update」。但 §7.4 的伪码实际调的是 `vipIncomeRecord(...)`（看起来是 insert 语义），没有给出「先查存在再决定 insert/update」的幂等判定，`income_pay_detail` 也没有 orderNo 唯一约束（现有表无 uk）。confirm 若重入，可能二次 insert 服务费收入 → 收入翻倍。
- **修复**：明确服务费**只在 `payFeeCallback` 入账一次**；过户 `transferEffect` 不再 insert 流水，只在需要时 `update income_pay_detail set another_id=? where order_no=?`（天然幂等）。把 §7.4 step3 的 `vipIncomeRecord` 改为纯 update，删掉「可能 insert」的歧义。

---

### 12.2 中危问题（M，逻辑漏洞/边界遗漏/不一致）

#### M-1 状态码冻结值 `2已冻结` 无入口、`vip_benefit` 状态机不完整
- **位置**：s1 §2.3 `vip_benefit.status=2 已冻结`、s5 §8.2 规则 3/5。
- **问题**：`vip_benefit.status` 定义了 `2 已冻结`，但**全设计无任何接口/任务把权益置 2**（拉黑会员后其在持权益是否冻结？无落点）。`transferable=0` 也只在 DDL 默认1，无任何接口把它改 0（s5 规则 5 校验它，但谁置 0？）。审核指标②要求「每个状态都有入口与出口」，`2` 与 `transferable=0` 均缺入口。
- **修复**：要么删除 `2已冻结`/`transferable` 的运行期语义（仅保留字段备用并在文档说明「本期不产生」），要么补一个 sys 接口（如黑名单生效时联动冻结该会员名下 `vip_benefit`），二选一明确。

#### M-2 `vip_benefit.status=9待支付` 的购买单超时/支付失败无清理，且 myBenefits 默认值表述矛盾
- **位置**：s4 §7.2、s2 §5.4。
- **问题**：购买先插 `status=9` 占位，但**未支付的 9 单永不回收**（无超时任务、无关闭接口）。长期堆积；更重要的是 §7.2.1 没说同一用户能否对同卡重复下单生成多条 9 单。s2 §5.4 入参说「不传=默认查 0正常」，但若用户有 9 单，列表逻辑未定义是否泄漏待支付实例。
- **修复**：① 明确 myBenefits 默认只查 `status=0`；② 补充待支付单的处理策略（可复用现有 `UpdateOrderStatusTask` 思路把超时 9 单置失效，或文档声明「9 单不清理、不展示、不影响」并说明为何可接受）。

#### M-3 停卡：有效期顺延的落点、`pause_days` 必填性、并发重复停卡均未闭环
- **位置**：s2 §5.11、s6 §6.6、s1 §2.5。
- **问题**：① s2 说「到期恢复由实时判断/定时任务把 status→1 并顺延 `expire_time`」，但**会员卡有效期在 `card_order`，本设计的顺延到底改哪张表哪个字段（card_order 的到期字段名）从未点名**，停卡顺延是核心却悬空。② `pause_days` 在 §5.11 标「否」，但停卡若不给天数，恢复时如何顺延？「停卡中」状态下 end_time=NULL，靠什么算顺延天数（按实际恢复时刻 vs 预设 pause_days）二义。③ 同卡并发两次 apply，月/年限次校验是「先查计数再插入」，无锁无唯一键，可击穿 12 次/月 1 次限制。
- **修复**：点名顺延作用的 `card_order` 字段与公式；明确 `pause_days` 是「预设固定时长」还是「恢复时实算」并二选一；月限次加唯一约束或 `for update` 串行化（如 `UNIQUE(card_order_id, pause_month)` 直接挡住每月第 2 次）。

#### M-4 撤回入口与契约/各章自相矛盾：到底 20 能不能撤回
- **位置**：s2 §5.8（仅 `status=40` 可撤回，且不退）vs s4 §7.3.7（`20` 撤回退费、`40` 撤回不退）vs s1 §3.3 状态机表（只画了 `40→60`，无 `20→60`）。
- **问题**：三处对「待审核(20)阶段能否撤回」结论不一致。s4 实现了 20/40 两路撤回，s2 只允许 40，s1 状态机图只有 40→60。这是审核指标②（每个状态出入口）+「各章一致」的双重违反。
- **修复**：拍板一个口径（建议采纳 s4：20 可撤回且退费、40 可撤回不退），同步修正 s2 §5.8 文案与 s1 §3.3 状态机表（补 `20→60 退费` 一行）。

#### M-5 H-1 之外，`vip_benefit.store_id` 在过户时被改、但 `store_addr_id` 不改，门店口径不一致
- **位置**：s4 §7.4「`UPDATE vip_benefit SET user_id=…, store_id=#{toStoreId} …`」。
- **问题**：过户把 `store_id` 改成受让人门店，但 `store_addr_id` 不动；而入场/适用门店判断常用 `store_addr_id`。改一个不改另一个,后续「适用门店」校验会出现 store_id 与 store_addr_id 指向不同门店的不一致。另外 toStoreId 的来源（s4 §7.3.1「toUser 归属门店」）取法未定义——用户可能多门店或无归属门店,等级校验(s5 规则16)会因 toStore=null 直接拦截。
- **修复**：明确过户是否改 store 归属（建议**不改**——权益的适用门店由 `vip_card.store_addr_ids` 决定，归属门店保持原样更简单，门店等级只在校验时用受让人「当前所属门店」即时查）；统一 store_id/store_addr_id 的处理。

#### M-6 `audit` 复核校验调 `checkTransferable` 需要 fromStore/toStore，但审核期 toUser 门店可能已变/为空，且 audit 在 sys.impl 调 api.impl 的 check 方法跨模块
- **位置**：s3 §6.3 step3「与 api 发起侧同一套校验方法」、s5 §8.2。
- **问题**：① `checkTransferable` 定义在 api.service.impl（s5），sys 审核要复用得跨模块注入或下沉到 common——文档未说放哪、怎么共享，落地会各写一份导致规则漂移（正是「apply 与 audit 两处都跑同一份」的初衷被破坏）。② audit 复核命中校验时 s3 说「按驳回处理」，但驳回要退费,而「因受让人后来被拉黑」驳回退费合理；可文档没说这种「复核驳回」与「人工驳回」是否都进 `status=31`、是否都退、`audit_remark` 如何区分。
- **修复**：把 `checkTransferable` 下沉到一个双模块都能调的位置（如 common 或一个被 api/sys 都依赖的 service），文档点名；明确复核失败统一走 31+退费,remark 标注「系统复核：xxx」。

#### M-7 退款记账（负向流水）只在 §7.5 一句「可视需要再写」，未定型 → 驳回/拒绝/超时退费后账面只有收入无退款流水
- **位置**：s4 §7.5 收尾。
- **问题**：退费场景（31/51/52/20撤回）只置 `refund_status=1`，退款的负向 `income_pay_detail` 写不写、用途码用几、谁来写都没定。现有 `PayServiceImpl.updateOrderStatus` 里商城退款用 `payType=9` 写负向流水有先例,本设计未对齐。结果：服务费收了又退,财务看到一笔收入、看不到退款 → 对账不平。
- **修复**：定义退款流水规范（新增退款用途码，或复用现有负向记账范式),在 `doRefund` 成功置位后补一条负向 `income_pay_detail`,写进 §7.5 与改动清单。

---

### 12.3 低危问题（L，文档/一致性/澄清）

- **L-1 `myBenefits` 用 `getUserId` 不校验封禁**（s2 §5.4），而其余资金类用 `getUserVo`；查询自己持有的卡用 getUserId 可接受,但与 §5.7「myList 也用 getUserId」一致即可,无需改,仅记录。
- **L-2 s5 §8.2 规则 5 文案「该权益已被冻结」与码名 `ERROR_VIP_BENEFIT_NOT_TRANSFERABLE`/契约「该权益不可转」语义偏移**；`transferable=0` 既可能是冻结也可能是商品本身设不可转,文案应中性化为「该权益当前不可转让」。
- **L-3 s2 §5.0 漏登记 `ERROR_VIP_BENEFIT_NOT_NORMAL`/`ERROR_VIP_CARD_OFF_SHELF` 等 s5 校验实际会抛的码**；apply 错误码清单与 checkTransferable 实抛集合不完全对齐（如 s2 §5.6 列了 -54 NOT_NORMAL,s5 抛的是 STATUS_ABNORMAL/EXPIRED/CARD_OFF_SHELF,码名码值都不同）。随 H-4 统一码表一并解决。
- **L-4 `confirm_deadline` 时限 N（默认 3 天）取自「配置项」但未指明配置在哪**（s3 §6.3、s4 §7.3.4）。建议明确放 `config.properties` 或写死常量,并与超时任务每小时扫描的精度说明（最坏延迟 1 小时关单）一并写清。
- **L-5 订单号长度核验通过**：`getOrderIdByTime()` = 14 位时间 + 9 位随机 = 23,+1 后缀 = 24 字符,`source_order_no`/`service_fee_order_no` VARCHAR(48) 充足；`uk_fee_order` 唯一键对幂等有用,但 `vip_benefit.source_order_no` **无唯一键**,§7.2.3 激活靠 `WHERE source_order_no=? AND status=9` 幂等,若同 orderNo 误插两条 9 单会都被激活——建议给 `source_order_no` 加唯一索引兜底。
- **L-6 `wxRefund` 失败返回 null 且内部吞异常**（已核验）：s4 §7.5「res==null 抛异常回滚」逻辑成立,但要注意 wxRefund 把网络异常也吞成 null,doRefund 抛 RRException 回滚后**状态停在原态可重试**,符合预期；仅需补充「退款失败的人工重试入口」（驳回失败单怎么再退）说明。
- **L-7 s6 §9.1 改动清单漏列 `IncomePayDetailServiceImpl` 改造**（见 H-1）、漏列「待支付 9 单/在途转让单」相关任务（见 M-2/H-5）。
- **L-8 `getUserVo(request)` 在 `rechargeCallBack` 顶部被调用**（现有代码 line 133），微信服务器回调无 token,该调用对新分支无影响（现有四类订单同样如此),仅记录不需改。

---

### 12.4 一致性核验结论（对照审核指标①②）

| 维度 | 结论 |
|---|---|
| 表名/字段（对 DDL） | 一致。各章引用的表名、列名与契约 DDL 吻合,未发现编造字段。 |
| 状态枚举值（vip_benefit / transfer / card / fee_rule / pause / blacklist） | 一致。各章 status 取值与契约枚举一一对应。 |
| **CodeAndMsg 码值/命名** | **不一致（H-4）**——s1/s2/s5/s6 同语义码值与命名互相冲突,必须统一码表。 |
| 转让状态机 10/20/31/40/51/52/60/70 入口与出口 | 基本完整,但 **20→60 撤回 在各章矛盾（M-4）**、`vip_benefit.status=2`/`transferable=0` 缺入口（M-1）。 |
| 方法名/签名（对真实代码） | **saveIncomePayDetail 签名与用法错（H-1/H-3）**、回调事务边界自述错（H-2）。其余 wxRefund/wxRechargePay/OrderNoGenerator 签名引用正确。 |
| 幂等/并发（带状态条件 UPDATE + FOR UPDATE） | 主链路（激活、付费回调、过户、撤回、超时）设计正确、到位；**但「在途多笔转让」并发场景有资金漏洞（H-5/H-6）**。 |
| 退费与 refund_status/wxRefund | 状态置位幂等正确;**退款负向流水未定型（M-7）**、复核驳回退费口径未明（M-6）。 |
| 前置校验覆盖 R-08/R-17 且 apply+audit 两跑 | 校验集 s5 §8.2 覆盖全面,但**共享落点跨模块未定（M-6）**,occupy/冻结缺失（H-5）。 |

**总评**：状态机与幂等骨架是这份设计最扎实的部分,直接可用;真正的拦路虎集中在「记账层与现有 `saveIncomePayDetail` 的对接（H-1/H-2/H-3/H-6/M-7）」和「CodeAndMsg 统一（H-4）」与「在途转让并发占用（H-5）」三块。建议在进入编码前先产出一节「附录 A 统一码表 + 附录 B 记账对接改造（含 IncomePayDetailServiceImpl 改动 + 退款流水规范）」,并就 M-4/M-3/M-5 三个口径分叉拍板,即可放行落地。
## 13. 附录（权威口径 · 高于第 5–11 章）

> 第 12 章评审对照真实代码挖出若干硬伤。**本附录是这些问题的最终拍板，凡与第 5–11 章冲突，一律以本附录为准。** 编码前必读。

### 附录 A — CodeAndMsg 统一码表（唯一事实来源，覆盖各章自定义）

各章自行分配的 VIP 码作废，统一用下表（连续编号，避开现有 -1~-36）。`CodeAndMsg` 枚举按此一次性加完：

| 枚举名 | 码 | 文案 |
|---|---|---|
| `ERROR_VIP_BENEFIT_NOT_EXIST` | -50 | 权益不存在 |
| `ERROR_VIP_BENEFIT_NOT_OWNER` | -51 | 该权益不属于你 |
| `ERROR_VIP_BENEFIT_STATUS_ABNORMAL` | -52 | 权益状态异常 |
| `ERROR_VIP_BENEFIT_EXPIRED` | -53 | 权益已过期 |
| `ERROR_VIP_BENEFIT_NOT_TRANSFERABLE` | -54 | 该权益当前不可转让 |
| `ERROR_VIP_CARD_OFF_SHELF` | -55 | 权益卡已下架 |
| `ERROR_VIP_TO_USER_NOT_EXIST` | -56 | 受让人不存在 |
| `ERROR_VIP_TO_USER_SELF` | -57 | 不能转让给自己 |
| `ERROR_VIP_USER_BLACKLIST` | -58 | 账号被封禁或在黑名单 |
| `ERROR_VIP_STORE_LEVEL_LIMIT` | -59 | 门店等级不满足（高级只能向等于/低于等级转让） |
| `ERROR_VIP_TRANSFER_NOT_EXIST` | -60 | 转让单不存在 |
| `ERROR_VIP_TRANSFER_STATUS` | -61 | 当前转让单状态不允许该操作 |
| `ERROR_VIP_TRANSFER_IN_PROGRESS` | -62 | 该权益已有进行中的转让 |
| `ERROR_VIP_TRANSFER_ARREARS` | -63 | 存在欠费，暂不可转让（口径待对齐） |
| `ERROR_VIP_TRANSFER_REFUNDED_CARD` | -64 | 已办理退卡，不可转让（口径待对齐） |
| `ERROR_VIP_TRANSFER_VIOLATION` | -65 | 存在重大违规记录（口径待对齐） |
| `ERROR_PAUSE_LIMIT_MONTH` | -66 | 本月已停卡一次 |
| `ERROR_PAUSE_LIMIT_YEAR` | -67 | 全年停卡次数已用完（共 12 次） |
| `ERROR_PAUSE_STATE` | -68 | 停卡状态异常 |
| `ERROR_VIP_FEE_RULE_NOT_EXIST` | -69 | 转让费用规则不存在/未配置 |
| `ERROR_VIP_FEE_RULE_FORMAT` | -70 | 分档配置格式有误 |

> 注：正文第 5、6、8 章中 `ERROR_VIP_FEE_RULE_NOT_EXIST`、`ERROR_VIP_FEE_RULE_FORMAT`、以及各 `ERROR_VIP_*` 的码值/命名以**本附录 A 表为准**（正文早稿中出现的 -55/-60/-63 等旧值作废）。

> 各章接口的"可能返回码"按本表对齐；前端按本表做文案/分支。

### 附录 B — 记账层对接改造（修 H-1 / H-2 / H-3 / H-6 / M-7）

**B.1 现状（已核验，必须照此理解）**
现有方法签名是 `IncomePayDetailServiceImpl.saveIncomePayDetail(String orderNo, String transactionNumber, BigDecimal money, Integer payType)`：
- 它内部 `setPayType(orderNo 末位数字)` —— 即 `income_pay_detail.payType` 存的是**交易用途（订单后缀）**，不是渠道；
- `setTradeType(传入的 payType)` —— 渠道（微信 WXPAY=2）落在 **`tradeType`**；
- userId / storeId 由内部 `if/else` **只对后缀 2/3/4/5** 赋值；**没有 anotherId 入参**。

**B.2 必改（写进"改动现有文件"清单 → `IncomePayDetailServiceImpl`，第 9 章漏列，补上）**
1. 新增后缀分支：
   - 后缀 `6`（权益卡购买）：按 `orderNo` 反查 `vip_benefit`（`source_order_no=orderNo`）取 `user_id / store_id` 填入。用途值 `payType=6`。
   - 后缀 `7`（转让费用）：按 `orderNo` 反查 `vip_benefit_transfer`（`service_fee_order_no=orderNo`）取 `from_user_id`→userId、`from_store_id`→storeId。用途值 `payType=7`。
2. 用途枚举登记：`income_pay_detail.payType` 新增 **6=权益卡购买、7=转让费用、8=转让退款（负向）**；对账/统计口径同步加这三类。
3. `anotherId` **不靠此方法写**：在过户生效时单独 `UPDATE income_pay_detail SET another_id=#{toUserId} WHERE order_no=#{serviceFeeOrderNo}`（天然幂等）。

**B.3 回调原子性（修 H-2：原写法"记账+激活+自增同生共死"不成立）**
`PayController.rechargeCallBack` 是 controller，**不在事务切面**。因此新订单类型回调必须把"记账 + 业务变更"封进**一个** `service.impl` 方法里，由 controller 只调它：
- 权益卡购买：回调分支只调 `vipBenefitService.activateByOrderNo(orderNo, transactionNumber)`；该方法内部（单事务，REQUIRED）依次：① `saveIncomePayDetail(orderNo, txn, money, WXPAY)`（记账，用途自动=6）；② `UPDATE vip_benefit SET status=0,start_time=now,expire_time=now+validity WHERE source_order_no=orderNo AND status=9`；③ 仅当 ②受影响行数=1 时 `UPDATE vip_benefit_card SET sold_count=sold_count+1 WHERE vip_card_id=?`。三步同事务、靠 ② 的状态条件幂等。
- 转让费用：回调分支只调 `vipTransferService.payFeeCallback(orderNo, transactionNumber)`；内部单事务：记账（用途=7）+ `UPDATE vip_benefit_transfer SET status=20,transaction_number=? WHERE service_fee_order_no=orderNo AND status=10`。
- **不要**在 controller 里分别调 `saveIncomePayDetail` 和 `updateXxx`（那是两个事务，会"有流水无业务"）。

**B.4 退款负向流水（修 M-7）**
`doRefund(transfer)` 调 `wxRefund` 成功并置 `refund_status=1` 后，**补一条负向流水**：`saveIncomePayDetail` 或直接 insert `income_pay_detail`（`payType=8 转让退款`、`money` 取负或用 tradeType 标记、`orderNo=service_fee_order_no`、`anotherId` 记原受让人）。与现有商城退款写负向流水的范式对齐。整个 doRefund 放在 service.impl（单事务）。

### 附录 C — 并发占用与幂等补强（修 H-5 / H-6 / L-5）

1. **在途转让唯一占用**：`apply` 进入时先 `SELECT ... FROM vip_benefit_transfer WHERE vip_benefit_id=? AND status IN (10,20,40) FOR UPDATE`，若存在则抛 `ERROR_VIP_TRANSFER_IN_PROGRESS(-62)`。保证一份权益同一时刻只有一笔在途转让，杜绝"并发两笔→一笔收费不退"。
2. **`vip_benefit.source_order_no` 加唯一索引** `UNIQUE KEY uk_source_order (source_order_no)`，兜底防止同单激活两条 9 单。
3. **过户 `transferEffect` 的 rows==0 不静默**：`UPDATE vip_benefit SET user_id=... WHERE vip_benefit_id=? AND user_id=#{fromUserId} AND status=0` 若 rows==0，要区分：
   - 本单 `vip_benefit_transfer.status` 已=70 → 幂等重入，直接返回；
   - 否则（被他单抢先/权益状态变了）→ 抛异常回滚，**本单转入失败态并触发退费**（不要让受让人收到"确认成功"却没拿到权益）。
   配合 C.1 的在途唯一占用，此情形已基本不会发生，但仍按此兜底。

### 附录 D — 三个口径分叉的拍板（修 M-1 / M-2 / M-3 / M-4 / M-5）

- **D.1 撤回（M-4）**：采纳"两段可撤回"——`status=20`（待审核）可撤回且**退费**；`status=40`（待受让人确认）可撤回但**不退费**。状态机补 `20→60(退费)` 与 `40→60(不退)` 两条。第 5 章 §5.8、第 1 章状态机图据此修正。
- **D.2 停卡顺延落点（M-3）**：顺延作用于会员卡到期日 **`card_order.validityDate`**。恢复时按**实际停卡天数**顺延：`validityDate = validityDate + (end_time - start_time 的天数)`，`pause_days` 记录实际天数（停卡中为空，恢复时回填）。每月限次用 **`UNIQUE KEY (card_order_id, pause_month)`** 在库层直接挡住"每月第 2 次"；全年 12 次用 `count(pause_month like '当年%')` 校验，命中抛 `ERROR_PAUSE_LIMIT_YEAR`。停卡 `apply` 用 `SELECT ... FOR UPDATE` 串行化防并发击穿。
- **D.3 门店归属与等级（M-5）**：过户**不改** `vip_benefit.store_id / store_addr_id`（归属保持原样）。门店等级校验取**双方当前所属门店**即时查：`fromUser`/`toUser` 的 `user_info.nowStoreId` → `store.store_level`，要求 `fromLevel >= toLevel`；若 `toUser.nowStoreId` 为空，按"无门店=最低等级"放行（即允许转入），避免空门店误拦。权益的"适用门店"始终由 `vip_benefit_card.store_addr_ids` 决定，与归属门店无关。
- **D.4 冻结/不可转字段（M-1）**：本期 `vip_benefit.status=2(已冻结)` 与 `transferable=0` **不在运行期产生**（字段保留备用）。"拉黑会员不可转让"由校验时**实时查 `member_blacklist` + `auditStatus=2`** 实现，不改权益本身。文档其余处对 `transferable=0` 的校验保留（兜底），但无接口主动置 0。
- **D.5 待支付单清理（M-2）**：`/api/vipCard/myBenefits` 默认只查 `status=0`；`status=9` 待支付单复用现有 `UpdateOrderStatusTask` 思路，超时（如 30 分钟未支付）置为失效（可新增 `status=8已关闭` 或直接物理/逻辑清理），不展示、不影响 sold_count。

### 附录 E — 前置校验落点（修 M-6）

`checkTransferable(...)` 统一实现在 **api 侧 `VipTransferServiceImpl`（api.service.impl）**。后台 `sys` 审核复用时，**直接 `@Autowired` 注入该 bean**——`sys` 与 `api` 同属一个 Spring 容器（同一 WAR），跨模块注入可行，无需下沉 common，保证"发起 + 审核两处跑同一份规则"。审核复核失败统一走 `status=31 已驳回 + 退费`，`audit_remark` 以"系统复核：xxx"标注，与人工驳回区分。

### 附录 F — 因本附录需追加/修订的改动清单（补第 9 章遗漏）

- **改** `IncomePayDetailServiceImpl`（B.2：后缀 6/7 分支 + 用途 6/7/8）。
- **改** `vip_benefit` DDL：加 `UNIQUE KEY uk_source_order (source_order_no)`（C.2）。
- **改** `card_pause_record` DDL：加 `UNIQUE KEY uk_card_month (card_order_id, pause_month)`（D.2）。
- **新增** 定时任务/复用 `UpdateOrderStatusTask`：超时关闭待支付权益单（D.5）。
- `CodeAndMsg`：按附录 A 一次性加全。
- 退款负向流水写入（B.4）纳入 `doRefund` 实现与对账口径。

---
