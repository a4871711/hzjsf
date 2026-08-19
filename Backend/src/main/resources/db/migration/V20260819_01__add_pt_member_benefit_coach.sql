-- 会员私教权益增加当前所属服务教练；不修改订单销售归属和历史预约教练。
-- 该脚本需要兼容部分环境已通过手工方式补过字段或索引的情况。
SET @benefit_coach_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pt_member_private_benefit'
    AND COLUMN_NAME = 'coach_id'
);

SET @benefit_coach_column_sql = IF(
  @benefit_coach_column_exists = 0,
  'ALTER TABLE `pt_member_private_benefit` ADD COLUMN `coach_id` BIGINT UNSIGNED DEFAULT NULL COMMENT ''当前所属服务教练ID；为空表示尚未指定'' AFTER `store_id`',
  'SELECT 1'
);
PREPARE benefit_coach_column_stmt FROM @benefit_coach_column_sql;
EXECUTE benefit_coach_column_stmt;
DEALLOCATE PREPARE benefit_coach_column_stmt;

SET @benefit_coach_index_exists = (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pt_member_private_benefit'
    AND INDEX_NAME = 'idx_pt_member_private_benefit_coach_id'
);

SET @benefit_coach_index_sql = IF(
  @benefit_coach_index_exists = 0,
  'ALTER TABLE `pt_member_private_benefit` ADD INDEX `idx_pt_member_private_benefit_coach_id` (`coach_id`)',
  'SELECT 1'
);
PREPARE benefit_coach_index_stmt FROM @benefit_coach_index_sql;
EXECUTE benefit_coach_index_stmt;
DEALLOCATE PREPARE benefit_coach_index_stmt;

-- 历史权益沿用来源订单的销售教练作为初始服务人；后续后台可单独调整。
UPDATE pt_member_private_benefit b
INNER JOIN pt_private_order o ON o.id = b.order_id
SET b.coach_id = o.coach_id
WHERE b.coach_id IS NULL
  AND o.coach_id IS NOT NULL;
