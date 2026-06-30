-- =====================================================================
-- 私教 · 教练等级表(新建·补充)
-- 第21节未给DDL,本期补;初始化四级,初级为默认
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

CREATE TABLE `pt_coach_level` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level_name` VARCHAR(50) NOT NULL COMMENT '等级名称,例如明星教练',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序权重,越大越靠前',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认:0否 1是,全表至多一条为1',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0停用 1启用',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除:0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_coach_level_name_deleted` (`level_name`, `deleted`),
  KEY `idx_pt_coach_level_status` (`status`),
  KEY `idx_pt_coach_level_sort` (`sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练等级表(第21节未给,本期补设计)';

INSERT INTO `pt_coach_level` (`level_name`, `sort_no`, `is_default`, `status`) VALUES
('初级教练', 10, 1, 1),
('中级教练', 20, 0, 1),
('高级教练', 30, 0, 1),
('明星教练', 40, 0, 1);
