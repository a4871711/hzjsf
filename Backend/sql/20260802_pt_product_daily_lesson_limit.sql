-- 私教商品：同一会员同一商品每日预约课时上限。
-- 默认 1 节，历史商品自动按 1 节补齐；可重复执行。

SET @daily_limit_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pt_product'
    AND COLUMN_NAME = 'daily_lesson_limit'
);
SET @daily_limit_column_sql = IF(
  @daily_limit_column_exists = 0,
  'ALTER TABLE `pt_product` ADD COLUMN `daily_lesson_limit` INT NOT NULL DEFAULT 1 COMMENT ''同一会员同一商品每日最多可预约课时数'' AFTER `booking_capacity`',
  'SELECT 1'
);
PREPARE daily_limit_column_stmt FROM @daily_limit_column_sql;
EXECUTE daily_limit_column_stmt;
DEALLOCATE PREPARE daily_limit_column_stmt;

SET @daily_limit_index_exists = (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pt_private_appointment'
    AND INDEX_NAME = 'idx_pt_appt_member_product_date_status'
);
SET @daily_limit_index_sql = IF(
  @daily_limit_index_exists = 0,
  'ALTER TABLE `pt_private_appointment` ADD INDEX `idx_pt_appt_member_product_date_status` (`member_id`, `product_id`, `appointment_date`, `appointment_status`)',
  'SELECT 1'
);
PREPARE daily_limit_index_stmt FROM @daily_limit_index_sql;
EXECUTE daily_limit_index_stmt;
DEALLOCATE PREPARE daily_limit_index_stmt;
