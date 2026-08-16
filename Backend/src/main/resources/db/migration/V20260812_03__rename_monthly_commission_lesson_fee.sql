-- 已创建教练包月提成表的环境，将“超标准单节提成”纠正为“小于标准单节提成”。
-- 旧列存在时才重命名；全新环境已由 V02 创建新列，本脚本会安全跳过。
SET @old_column_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'pt_coach_monthly_commission_rule'
    AND column_name = 'excess_lesson_fee'
);

SET @rename_sql = IF(
  @old_column_exists > 0,
  'ALTER TABLE `pt_coach_monthly_commission_rule` CHANGE COLUMN `excess_lesson_fee` `below_standard_lesson_fee` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT ''教练累计完课小于标准课节时的每节提成金额''',
  'SELECT 1'
);

PREPARE rename_stmt FROM @rename_sql;
EXECUTE rename_stmt;
DEALLOCATE PREPARE rename_stmt;
