-- 私教商品可单独控制会员手机端是否展示课时数量；历史商品保持原有展示效果。
SET @lesson_count_visible_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'pt_product'
    AND COLUMN_NAME = 'lesson_count_visible'
);

SET @lesson_count_visible_column_sql = IF(
  @lesson_count_visible_column_exists = 0,
  'ALTER TABLE `pt_product` ADD COLUMN `lesson_count_visible` TINYINT NOT NULL DEFAULT 1 COMMENT ''手机端是否显示课时数量：0否 1是'' AFTER `lesson_count`',
  'SELECT 1'
);
PREPARE lesson_count_visible_column_stmt FROM @lesson_count_visible_column_sql;
EXECUTE lesson_count_visible_column_stmt;
DEALLOCATE PREPARE lesson_count_visible_column_stmt;
