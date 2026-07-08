-- =====================================================================
-- 停卡功能重构 v2 — 数据库变更脚本
-- ① 新表 vip_pause_rule：停卡付费规则(多档阶梯:天数/价格)，照 vip_fee_rule 模式
-- ② vip_benefit_card 新增 pause_rule_id：权益卡商品绑定停卡规则(一对一)
-- ③ card_pause_record 升级：废弃"每自然月1次/全年12次"，改为
--    免费额度(按用户滚动30天1次,每次≤7天) + 付费停卡(按规则档位,微信支付)；
--    停卡改为固定天数"申请时预顺延"，支持提前取消扣回未用天数
-- ④ 后台菜单：「停卡规则」挂「VIP权益」目录下
--
-- 部署顺序：本脚本必须先于新代码部署执行。CardPauseRecordMapper.xml /
-- VipBenefitCardDao.xml 均为手写显式列清单(非 SELECT *)，列不存在时会直接报
-- SQLSyntaxErrorException: Unknown column 'xxx' in 'field list'。
-- =====================================================================

-- 1) 停卡付费规则表(结构照 vip_fee_rule)
CREATE TABLE IF NOT EXISTS `vip_pause_rule` (
  `pause_rule_id` BIGINT        NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name`     VARCHAR(64)   NOT NULL COMMENT '规则名称',
  `tiers_json`    VARCHAR(2048) NOT NULL COMMENT '分档配置JSON数组,如[{"days":3,"price":100},{"days":10,"price":200}]',
  `status`        TINYINT       NOT NULL DEFAULT 1 COMMENT '状态:1启用 0停用(停用后按无付费停卡兜底)',
  `created_date`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`pause_rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP停卡付费规则';

-- 2) vip_benefit_card：绑定停卡规则(一张权益卡最多绑一条)。
--    NULL = 不绑定，该卡会员只有免费停卡额度、无付费停卡；向后兼容全部存量配置。
ALTER TABLE `vip_benefit_card`
  ADD COLUMN `pause_rule_id` BIGINT DEFAULT NULL
  COMMENT '关联停卡规则(vip_pause_rule),NULL=不关联(仅免费停卡额度)'
  AFTER `fee_rule_id`;

-- 3) card_pause_record 升级：
--    删每月唯一键与 pause_month(旧限制废弃)；新增免费/付费、支付、取消相关列。
--    status 语义变更：10待支付 0生效(到期后查询时判"已结束",不写库)
--                     1已恢复(仅存量历史) 2已取消(提前恢复) 3已关闭(未支付被顶替)
ALTER TABLE `card_pause_record`
  DROP INDEX `uk_card_month`,
  DROP COLUMN `pause_month`,
  ADD COLUMN `pause_type`     TINYINT NOT NULL DEFAULT 0 COMMENT '停卡类型:0免费 1付费' AFTER `card_order_id`,
  ADD COLUMN `pause_rule_id`  BIGINT DEFAULT NULL COMMENT '付费停卡所用规则(vip_pause_rule),免费为NULL' AFTER `pause_type`,
  ADD COLUMN `amount`         DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '付费金额(元),免费=0' AFTER `pause_rule_id`,
  ADD COLUMN `pay_order_no`   VARCHAR(40) DEFAULT NULL COMMENT '停卡支付单号(末位后缀c),免费为NULL' AFTER `amount`,
  ADD COLUMN `transaction_id` VARCHAR(64) DEFAULT NULL COMMENT '微信支付流水号' AFTER `pay_order_no`,
  ADD COLUMN `pay_time`       DATETIME DEFAULT NULL COMMENT '支付成功时间' AFTER `transaction_id`,
  ADD COLUMN `cancel_time`    DATETIME DEFAULT NULL COMMENT '提前取消时间' AFTER `end_time`,
  ADD COLUMN `actual_days`    INT DEFAULT NULL COMMENT '取消时实际已停天数(不足1天按1天)' AFTER `cancel_time`,
  MODIFY COLUMN `pause_days` INT DEFAULT NULL COMMENT '停卡天数(免费1~7自选;付费=规则档位天数;旧数据为恢复时回填)',
  MODIFY COLUMN `end_time` DATETIME DEFAULT NULL COMMENT '计划结束时间=start_time+pause_days(旧数据为恢复时间;NULL=旧开放式停卡中)',
  MODIFY COLUMN `status` TINYINT NOT NULL DEFAULT 0 COMMENT '10待支付 0生效 1已恢复(存量历史) 2已取消 3已关闭(未支付)',
  ADD UNIQUE KEY `uk_pause_pay_order` (`pay_order_no`),
  ADD KEY `idx_user_type_start` (`user_id`, `pause_type`, `start_time`);

-- 4) 「停卡规则」菜单(type=1,挂「VIP权益」目录下)→ views/modules/sys/vipPauseRule.vue
--    NOT EXISTS 保证可重复执行不插重复(照 sys_menu_vip.sql 写法)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '停卡规则', 'modules/sys/vipPauseRule.html',
       'sys:vipPauseRule:list,sys:vipPauseRule:info,sys:vipPauseRule:save,sys:vipPauseRule:update,sys:vipPauseRule:delete',
       1, 'fa fa-pause', 6
FROM (SELECT menu_id FROM sys_menu WHERE name='VIP权益' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/vipPauseRule.html');
