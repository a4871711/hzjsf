-- =====================================================================
-- 私教 · 资金域建表(分期计划/账单执行)
-- pt_order_installment_plan, pt_order_installment_bill(+支付单幂等键);注:pt_installment_rule建表归商品域,本处不建
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

CREATE TABLE `pt_order_installment_plan` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT UNSIGNED NOT NULL COMMENT '私教订单ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '私教商品ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `down_payment_amount` DECIMAL(10,2) NOT NULL COMMENT '首付金额',
  `paid_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已付金额',
  `unpaid_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '未付金额',
  `installment_count` INT NOT NULL COMMENT '总期数',
  `current_period` INT NOT NULL DEFAULT 1 COMMENT '当前期数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '分期状态：1进行中 2已结清 3已逾期 4已关闭',
  `activated_at` DATETIME DEFAULT NULL COMMENT '权益激活时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_order_installment_plan_order_id` (`order_id`),
  KEY `idx_pt_order_installment_plan_member_id` (`member_id`),
  KEY `idx_pt_order_installment_plan_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教订单分期计划表';

CREATE TABLE `pt_order_installment_bill` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` BIGINT UNSIGNED NOT NULL COMMENT '分期计划ID',
  `period_no` INT NOT NULL COMMENT '期数',
  `due_amount` DECIMAL(10,2) NOT NULL COMMENT '应付金额',
  `paid_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  `due_date` DATE NOT NULL COMMENT '应付日期',
  `paid_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '账单状态：0待支付 1已支付 2已逾期 3已关闭',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `pay_order_no` VARCHAR(64) DEFAULT NULL COMMENT '本期支付单号,回调入账幂等用', -- 【补充】
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_order_installment_bill_period` (`plan_id`, `period_no`),
  KEY `idx_pt_order_installment_bill_status` (`status`),
  KEY `idx_pt_order_installment_bill_due_date` (`due_date`),
  UNIQUE KEY `uk_pt_order_installment_bill_pay_order_no` (`pay_order_no`) -- 【补充】
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教订单分期账单表';

