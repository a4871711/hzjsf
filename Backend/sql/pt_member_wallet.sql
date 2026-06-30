-- =====================================================================
-- 私教 · 资金域建表(会员储值账户/流水)
-- pt_member_wallet, pt_member_wallet_flow(+外部单号幂等键)
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

CREATE TABLE `pt_member_wallet` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `balance_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '当前储值余额',
  `total_recharge_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计充值金额',
  `total_consume_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 2冻结',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_member_wallet_member_id` (`member_id`),
  KEY `idx_pt_member_wallet_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员储值账户表';

CREATE TABLE `pt_member_wallet_flow` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `wallet_id` BIGINT UNSIGNED NOT NULL COMMENT '储值账户ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `flow_type` TINYINT NOT NULL COMMENT '流水类型：1充值 2消费 3退款 4冲正',
  `change_amount` DECIMAL(10,2) NOT NULL COMMENT '变动金额',
  `before_balance` DECIMAL(10,2) NOT NULL COMMENT '变动前余额',
  `after_balance` DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
  `biz_type` TINYINT DEFAULT NULL COMMENT '关联业务类型：1订单 2退款单 3人工调整',
  `biz_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联业务ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `out_order_no` VARCHAR(64) DEFAULT NULL COMMENT '外部业务单号(充值/退款),回调幂等用', -- 【补充】
  PRIMARY KEY (`id`),
  KEY `idx_pt_member_wallet_flow_wallet_id` (`wallet_id`),
  KEY `idx_pt_member_wallet_flow_member_id` (`member_id`),
  KEY `idx_pt_member_wallet_flow_type` (`flow_type`),
  UNIQUE KEY `uk_pt_member_wallet_flow_out_order_no` (`out_order_no`) -- 【补充】
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员储值流水表';

