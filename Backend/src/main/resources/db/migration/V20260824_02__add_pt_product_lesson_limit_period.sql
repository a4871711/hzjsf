-- 将原“每日预约上限”扩展为“每 X 天最多上 Y 节”；历史商品 X 默认为 1，行为保持不变。
SET @lesson_limit_period_days_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pt_product'
    AND COLUMN_NAME = 'lesson_limit_period_days'
);

SET @lesson_limit_period_days_column_sql = IF(
  @lesson_limit_period_days_column_exists = 0,
  'ALTER TABLE `pt_product` ADD COLUMN `lesson_limit_period_days` INT NOT NULL DEFAULT 1 COMMENT ''固定预约周期天数；从权益生效日开始计算'' AFTER `booking_capacity`',
  'SELECT 1'
);
PREPARE lesson_limit_period_days_column_stmt FROM @lesson_limit_period_days_column_sql;
EXECUTE lesson_limit_period_days_column_stmt;
DEALLOCATE PREPARE lesson_limit_period_days_column_stmt;

SET @lesson_limit_period_index_exists = (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pt_private_appointment'
    AND INDEX_NAME = 'idx_pt_appt_benefit_date_status'
);

SET @lesson_limit_period_index_sql = IF(
  @lesson_limit_period_index_exists = 0,
  'ALTER TABLE `pt_private_appointment` ADD INDEX `idx_pt_appt_benefit_date_status` (`benefit_id`, `appointment_date`, `appointment_status`)',
  'SELECT 1'
);
PREPARE lesson_limit_period_index_stmt FROM @lesson_limit_period_index_sql;
EXECUTE lesson_limit_period_index_stmt;
DEALLOCATE PREPARE lesson_limit_period_index_stmt;
