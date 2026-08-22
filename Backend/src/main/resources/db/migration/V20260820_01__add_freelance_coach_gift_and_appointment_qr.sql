-- 自由教练赠课与无会籍卡预约二维码开门。
-- 所有 DDL 均先检查元数据，兼容部分环境已手工补字段的历史情况。

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_coach' AND COLUMN_NAME = 'coach_type') = 0,
  'ALTER TABLE `pt_coach` ADD COLUMN `coach_type` TINYINT NOT NULL DEFAULT 1 COMMENT ''教练类型：1私教 2自由教练'' AFTER `user_id`',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order' AND COLUMN_NAME = 'order_source') = 0,
  'ALTER TABLE `pt_private_order` ADD COLUMN `order_source` TINYINT NOT NULL DEFAULT 0 COMMENT ''订单来源：0正常购买 1赠送'' AFTER `order_status`',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order' AND COLUMN_NAME = 'source_order_id') = 0,
  'ALTER TABLE `pt_private_order` ADD COLUMN `source_order_id` BIGINT UNSIGNED DEFAULT NULL COMMENT ''赠送来源订单ID'' AFTER `order_source`',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order' AND COLUMN_NAME = 'source_benefit_id') = 0,
  'ALTER TABLE `pt_private_order` ADD COLUMN `source_benefit_id` BIGINT UNSIGNED DEFAULT NULL COMMENT ''赠送来源权益ID'' AFTER `source_order_id`',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order' AND COLUMN_NAME = 'gift_request_no') = 0,
  'ALTER TABLE `pt_private_order` ADD COLUMN `gift_request_no` VARCHAR(64) DEFAULT NULL COMMENT ''赠课请求幂等号'' AFTER `source_benefit_id`',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order' AND COLUMN_NAME = 'remark') = 0,
  'ALTER TABLE `pt_private_order` ADD COLUMN `remark` VARCHAR(255) DEFAULT NULL COMMENT ''订单备注'' AFTER `gift_request_no`',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'about_us' AND COLUMN_NAME = 'appointment_qr_enabled') = 0,
  'ALTER TABLE `about_us` ADD COLUMN `appointment_qr_enabled` TINYINT NOT NULL DEFAULT 0 COMMENT ''无会籍卡预约开门码：0关闭 1开启'' AFTER `qrcode_valid`',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order'
     AND INDEX_NAME = 'uk_pt_private_order_gift_request_no') = 0,
  'ALTER TABLE `pt_private_order` ADD UNIQUE INDEX `uk_pt_private_order_gift_request_no` (`gift_request_no`)',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order'
     AND INDEX_NAME = 'idx_pt_private_order_source_order') = 0,
  'ALTER TABLE `pt_private_order` ADD INDEX `idx_pt_private_order_source_order` (`source_order_id`)',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order'
     AND INDEX_NAME = 'idx_pt_private_order_source_benefit') = 0,
  'ALTER TABLE `pt_private_order` ADD INDEX `idx_pt_private_order_source_benefit` (`source_benefit_id`)',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

-- 历史数据显式归类，便于严格枚举校验和审计查询。
UPDATE `pt_coach` SET `coach_type` = 1 WHERE `coach_type` IS NULL OR `coach_type` NOT IN (1, 2);
UPDATE `pt_private_order` SET `order_source` = 0 WHERE `order_source` IS NULL OR `order_source` NOT IN (0, 1);
UPDATE `about_us` SET `appointment_qr_enabled` = 0
WHERE `appointment_qr_enabled` IS NULL OR `appointment_qr_enabled` NOT IN (0, 1);

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_coach'
     AND CONSTRAINT_NAME = 'chk_pt_coach_type') = 0,
  'ALTER TABLE `pt_coach` ADD CONSTRAINT `chk_pt_coach_type` CHECK (`coach_type` IN (1, 2))',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_private_order'
     AND CONSTRAINT_NAME = 'chk_pt_private_order_source') = 0,
  'ALTER TABLE `pt_private_order` ADD CONSTRAINT `chk_pt_private_order_source` CHECK (`order_source` IN (0, 1))',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;

SET @ddl_sql = IF(
  (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'about_us'
     AND CONSTRAINT_NAME = 'chk_about_us_appointment_qr') = 0,
  'ALTER TABLE `about_us` ADD CONSTRAINT `chk_about_us_appointment_qr` CHECK (`appointment_qr_enabled` IN (0, 1))',
  'SELECT 1'
);
PREPARE ddl_stmt FROM @ddl_sql;
EXECUTE ddl_stmt;
DEALLOCATE PREPARE ddl_stmt;
