-- =====================================================================
-- 私教 · 教练域建表(教练/门店关联/排班)
-- pt_coach, pt_coach_store_rel, pt_coach_schedule
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

CREATE TABLE `pt_coach` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coach_no` VARCHAR(32) NOT NULL COMMENT '教练编号，例如 JL202606020001',
  `coach_name` VARCHAR(50) NOT NULL COMMENT '教练姓名',
  `mobile` VARCHAR(20) NOT NULL COMMENT '手机号',
  `gender` TINYINT DEFAULT NULL COMMENT '性别：1男 2女 0未知',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  `coach_level` VARCHAR(50) DEFAULT NULL COMMENT '教练等级',
  `intro` VARCHAR(1000) DEFAULT NULL COMMENT '教练简介',
  `certificate_urls` JSON DEFAULT NULL COMMENT '资格证书图片或附件地址',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '教练状态：1正常 2停用 3离职',
  `disable_reason` VARCHAR(255) DEFAULT NULL COMMENT '停用原因',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_coach_no` (`coach_no`),
  UNIQUE KEY `uk_pt_coach_mobile` (`mobile`),
  KEY `idx_pt_coach_name` (`coach_name`),
  KEY `idx_pt_coach_status` (`status`),
  KEY `idx_pt_coach_level` (`coach_level`),
  KEY `idx_pt_coach_sort` (`sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教教练主表';

CREATE TABLE `pt_coach_store_rel` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coach_id` BIGINT UNSIGNED NOT NULL COMMENT '教练ID',
  `store_id` BIGINT UNSIGNED NOT NULL COMMENT '门店ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_coach_store` (`coach_id`, `store_id`),
  KEY `idx_pt_coach_store_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练所属门店关联表';

CREATE TABLE `pt_coach_schedule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coach_id` BIGINT UNSIGNED NOT NULL COMMENT '教练ID',
  `store_id` BIGINT UNSIGNED NOT NULL COMMENT '排班所属门店ID',
  `weekday` TINYINT NOT NULL COMMENT '星期：1周一 2周二 3周三 4周四 5周五 6周六 7周日',
  `start_time` TIME NOT NULL COMMENT '开始时间',
  `end_time` TIME NOT NULL COMMENT '结束时间',
  `is_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0否 1是',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_coach_schedule_coach_id` (`coach_id`),
  KEY `idx_pt_coach_schedule_store_id` (`store_id`),
  KEY `idx_pt_coach_schedule_weekday` (`weekday`),
  KEY `idx_pt_coach_schedule_enabled` (`is_enabled`),
  KEY `idx_pt_coach_schedule_lookup` (`coach_id`, `store_id`, `weekday`, `is_enabled`) -- 【补充】
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练固定周排班表';

