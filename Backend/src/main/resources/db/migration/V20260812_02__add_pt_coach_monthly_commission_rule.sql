-- 包月课程不使用普通教练分成规则，按教练+包月商品独立配置提成。
CREATE TABLE IF NOT EXISTS `pt_coach_monthly_commission_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coach_id` BIGINT UNSIGNED NOT NULL COMMENT '私教教练ID',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '包月私教商品ID',
  `standard_lesson_count` INT NOT NULL COMMENT '提成标准课节，未达到时按固定单节提成，达到后按比例结算',
  `commission_rate` DECIMAL(5,2) NOT NULL COMMENT '标准课节内提成比例(%)',
  `below_standard_lesson_fee` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '教练累计完课小于标准课节时的每节提成金额',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_coach_monthly_rule_coach_product` (`coach_id`, `product_id`),
  KEY `idx_pt_coach_monthly_rule_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练包月课程提成配置';
