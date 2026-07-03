-- =====================================================================
-- 私教 · 教练课时费/分成规则表(扩展版)
-- 跨域同名表统一仲裁:全系统只此一处建表(扩展版),运营域只读引用(见总则0.11#1)
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

CREATE TABLE `pt_coach_fee_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coach_id` BIGINT UNSIGNED NOT NULL COMMENT '教练ID',
  `product_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '私教商品ID,0=不限(按教练统一)',
  `store_id` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '适用门店ID,0=全部门店', -- 【补充】扩展:门店维度
  `rule_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '规则名称,便于后台识别', -- 【补充】
  `rule_type` TINYINT NOT NULL DEFAULT 1 COMMENT '规则类型:1课时费 2销售提成', -- 【补充】
  `lesson_fee` DECIMAL(10,2) DEFAULT NULL COMMENT '单次课时费(rule_type=1);提成规则该列空', -- 【调整】由NOT NULL放宽为DEFAULT NULL
  `commission_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '销售提成比例%(rule_type=2必填)', -- 【补充】
  `effective_time` DATETIME DEFAULT NULL COMMENT '生效时间,NULL=立即生效;命中需 effective_time<=now', -- 【补充】
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1启用 0停用',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_coach_fee_rule` (`coach_id`, `product_id`, `store_id`, `rule_type`), -- 【调整】原(coach_id,product_id)加 store_id,rule_type
  KEY `idx_pt_coach_fee_rule_coach` (`coach_id`, `status`), -- 【补充】结算捞该教练全部启用规则
  KEY `idx_pt_coach_fee_rule_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练课时费/分成规则表(扩展版;全系统唯一建表口径,运营域报表只读引用)';
