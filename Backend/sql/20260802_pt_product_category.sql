-- =====================================================================
-- 私教商品分类配置化迁移
-- 1. 新建分类配置表；2. 增加 pt_product.category_id；3. 迁移历史分类；4. 新增后台菜单。
-- 执行前请备份目标数据库。本脚本面向 MySQL 5.7/8.0，可重复执行。
-- =====================================================================

CREATE TABLE IF NOT EXISTS `pt_product_category` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_product_category_name_deleted` (`category_name`, `deleted`),
  KEY `idx_pt_product_category_status` (`status`),
  KEY `idx_pt_product_category_sort` (`sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品分类表';

INSERT INTO pt_product_category (category_name, sort_no, status)
SELECT '增肌', 100, 1 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM pt_product_category WHERE deleted = 0 AND category_name = '增肌');
INSERT INTO pt_product_category (category_name, sort_no, status)
SELECT '减脂', 90, 1 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM pt_product_category WHERE deleted = 0 AND category_name = '减脂');
INSERT INTO pt_product_category (category_name, sort_no, status)
SELECT '塑形', 80, 1 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM pt_product_category WHERE deleted = 0 AND category_name = '塑形');
INSERT INTO pt_product_category (category_name, sort_no, status)
SELECT '康复', 70, 1 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM pt_product_category WHERE deleted = 0 AND category_name = '康复');
INSERT INTO pt_product_category (category_name, sort_no, status)
SELECT '综合训练', 60, 1 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM pt_product_category WHERE deleted = 0 AND category_name = '综合训练');

-- 把历史自由输入的分类全部纳入配置表。
INSERT INTO pt_product_category (category_name, sort_no, status)
SELECT DISTINCT TRIM(p.category_name), 50, 1
FROM pt_product p
WHERE p.category_name IS NOT NULL
  AND TRIM(p.category_name) <> ''
  AND NOT EXISTS (
      SELECT 1 FROM pt_product_category c
      WHERE c.deleted = 0 AND c.category_name = TRIM(p.category_name)
  );

SET @category_column_exists = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_product' AND COLUMN_NAME = 'category_id'
);
SET @add_category_column_sql = IF(
  @category_column_exists = 0,
  'ALTER TABLE pt_product ADD COLUMN category_id BIGINT UNSIGNED NULL COMMENT ''商品分类ID，关联pt_product_category'' AFTER service_type',
  'SELECT 1'
);
PREPARE add_category_column_stmt FROM @add_category_column_sql;
EXECUTE add_category_column_stmt;
DEALLOCATE PREPARE add_category_column_stmt;

SET @category_index_exists = (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pt_product' AND INDEX_NAME = 'idx_pt_product_category_id'
);
SET @add_category_index_sql = IF(
  @category_index_exists = 0,
  'ALTER TABLE pt_product ADD INDEX idx_pt_product_category_id (category_id)',
  'SELECT 1'
);
PREPARE add_category_index_stmt FROM @add_category_index_sql;
EXECUTE add_category_index_stmt;
DEALLOCATE PREPARE add_category_index_stmt;

UPDATE pt_product p
INNER JOIN pt_product_category c
        ON c.deleted = 0 AND c.category_name = TRIM(p.category_name)
SET p.category_id = c.id
WHERE p.category_name IS NOT NULL AND TRIM(p.category_name) <> '';

-- 动态菜单：分类管理位于商品类型管理和私教商品之间。
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '商品分类管理', 'modules/sys/ptProductCategory.html',
       'sys:ptproductcategory:list,sys:ptproductcategory:info,sys:ptproductcategory:save,sys:ptproductcategory:update,sys:ptproductcategory:delete,sys:ptproductcategory:updateStatus',
       1, 'fa fa-list-alt', 6
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/ptProductCategory.html');

UPDATE sys_menu SET order_num = 5 WHERE url = 'modules/sys/ptProductType.html';
UPDATE sys_menu SET order_num = 6 WHERE url = 'modules/sys/ptProductCategory.html';
UPDATE sys_menu SET order_num = 7 WHERE url = 'modules/sys/ptProduct.html';
UPDATE sys_menu SET order_num = 8 WHERE url = 'modules/sys/ptMemberGroupBenefit.html';

-- 执行后核对：历史商品分类应全部回填 category_id。
SELECT p.id, p.product_no, p.product_name, p.category_id, p.category_name
FROM pt_product p
WHERE p.deleted = 0
ORDER BY p.id DESC;
