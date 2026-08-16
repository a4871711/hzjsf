-- 私教教练绑定手机端会员账号。
-- user_id 可空：历史教练默认未绑定；唯一索引保证一个会员账号最多绑定一名私教。

SET @pt_coach_user_col_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'pt_coach'
      AND column_name = 'user_id'
);

SET @pt_coach_user_col_ddl = IF(
    @pt_coach_user_col_exists = 0,
    'ALTER TABLE pt_coach ADD COLUMN user_id BIGINT DEFAULT NULL COMMENT ''绑定的手机端会员 user_info.userId'' AFTER mobile',
    'SELECT 1'
);

PREPARE stmt FROM @pt_coach_user_col_ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @pt_coach_user_idx_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'pt_coach'
      AND index_name = 'uk_pt_coach_user_id'
);

SET @pt_coach_user_idx_ddl = IF(
    @pt_coach_user_idx_exists = 0,
    'ALTER TABLE pt_coach ADD UNIQUE KEY uk_pt_coach_user_id (user_id)',
    'SELECT 1'
);

PREPARE stmt FROM @pt_coach_user_idx_ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
