-- 私教教练提现：申请时冻结，审核通过时按实际结算金额扣减。
-- 结算金额与实际结算金额分开保存，附件使用 JSON 数组字符串保存。
CREATE TABLE IF NOT EXISTS `pt_coach_withdrawal` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '提现申请ID',
  `coach_id` BIGINT UNSIGNED NOT NULL COMMENT '私教教练ID',
  `requested_amount` DECIMAL(12,2) NOT NULL COMMENT '教练申请金额',
  `settlement_amount` DECIMAL(12,2) NOT NULL COMMENT '审核结算金额',
  `actual_settlement_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '实际结算金额；审核通过时扣减该金额',
  `frozen_amount` DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '当前冻结金额；待审核时等于申请金额',
  `account_name` VARCHAR(100) NOT NULL COMMENT '收款人姓名',
  `bank_name` VARCHAR(100) NOT NULL COMMENT '开户行名称',
  `bank_card_no` VARCHAR(32) NOT NULL COMMENT '银行卡号',
  `attachment_urls` TEXT COMMENT '审核附件地址 JSON 数组',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待审核 1已驳回 2已通过',
  `review_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `reviewed_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '审核人ID',
  `reviewed_at` DATETIME DEFAULT NULL COMMENT '审核时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_coach_withdrawal_coach_status` (`coach_id`, `status`),
  KEY `idx_pt_coach_withdrawal_status_created` (`status`, `created_at`),
  KEY `idx_pt_coach_withdrawal_coach_created` (`coach_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教教练提现申请';

-- 后台前端使用动态菜单路由，菜单按 URL 防重。
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT 0, '私教管理', NULL, NULL, 0, 'fa fa-user-circle-o', 20
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.name='私教管理' AND m.parent_id=0);

INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '提现审核', 'modules/sys/ptCoachWithdrawal.html',
       'sys:ptCoachWithdrawal:list,sys:ptCoachWithdrawal:info,sys:ptCoachWithdrawal:review',
       1, 'fa fa-credit-card', 5
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/ptCoachWithdrawal.html');
