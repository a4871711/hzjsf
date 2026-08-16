-- 私教提成结算依赖订单上的销售归属教练快照。
-- 部分环境可能已经执行过旧目录 Backend/sql/20260728_pt_commission_settlement.sql，
-- 因此先检查字段是否存在，避免 Flyway 启动时因 Duplicate column name 失败。
SET @col_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'pt_private_order'
      AND column_name = 'coach_id'
);

SET @ddl = IF(
    @col_exists = 0,
    'ALTER TABLE pt_private_order ADD COLUMN coach_id BIGINT UNSIGNED DEFAULT NULL COMMENT ''销售归属教练快照；商品仅指定一名教练时记录'' AFTER store_id',
    'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
