-- =====================================================================
-- 私教 · 运营域建表(评价/续费预警/跨店/异常预警/团课转私教)
-- 评价2+续费预警3+跨店1+异常2+团课转私教3=11表;不含 pt_coach_fee_rule(归教练域,见总则0.11#1)
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

CREATE TABLE `pt_coach_comment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `appointment_id` BIGINT UNSIGNED NOT NULL COMMENT '关联预约ID',
  `coach_id` BIGINT UNSIGNED NOT NULL COMMENT '教练ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `member_nickname` VARCHAR(100) DEFAULT NULL COMMENT '会员昵称',
  `member_mobile` VARCHAR(20) DEFAULT NULL COMMENT '会员手机号',
  `score` TINYINT NOT NULL COMMENT '评分：1-5星',
  `comment_content` VARCHAR(1000) DEFAULT NULL COMMENT '评价内容',
  `reply_status` TINYINT NOT NULL DEFAULT 0 COMMENT '回复状态：0未回复 1已回复',
  `handle_status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态：0待处理 1已跟进 2已忽略',
  `handle_remark` VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
  `comment_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_coach_comment_appointment_id` (`appointment_id`),
  KEY `idx_pt_coach_comment_coach_id` (`coach_id`),
  KEY `idx_pt_coach_comment_member_id` (`member_id`),
  KEY `idx_pt_coach_comment_score` (`score`),
  KEY `idx_pt_coach_comment_reply_status` (`reply_status`),
  KEY `idx_pt_coach_comment_handle_status` (`handle_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练评价表';

CREATE TABLE `pt_coach_comment_reply` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` BIGINT UNSIGNED NOT NULL COMMENT '评价ID',
  `reply_content` VARCHAR(1000) NOT NULL COMMENT '回复内容',
  `reply_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '回复人ID',
  `reply_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_coach_comment_reply_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练评价回复表';

CREATE TABLE `pt_renewal_warning_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `lesson_warning_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用剩余课时预警：0否 1是',
  `lesson_threshold` INT DEFAULT NULL COMMENT '剩余课时阈值',
  `days_warning_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用剩余天数预警：0否 1是',
  `days_threshold` INT DEFAULT NULL COMMENT '剩余天数阈值',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '规则状态：1启用 0停用',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_renewal_warning_rule_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='续费预警规则表，提醒对象固定为教练';

CREATE TABLE `pt_renewal_warning_rule_store_rel` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_id` BIGINT UNSIGNED NOT NULL COMMENT '规则ID',
  `store_id` BIGINT UNSIGNED NOT NULL COMMENT '门店ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_renewal_warning_rule_store` (`rule_id`, `store_id`),
  KEY `idx_pt_renewal_warning_rule_store_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='续费预警规则适用门店关联表';

CREATE TABLE `pt_renewal_warning_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_id` BIGINT UNSIGNED NOT NULL COMMENT '规则ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `benefit_id` BIGINT UNSIGNED NOT NULL COMMENT '会员私教权益ID',
  `coach_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联教练ID',
  `store_id` BIGINT UNSIGNED NOT NULL COMMENT '门店ID',
  `product_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联私教商品ID',
  `remaining_lessons` INT DEFAULT NULL COMMENT '剩余课时',
  `remaining_days` INT DEFAULT NULL COMMENT '剩余天数',
  `warning_type` TINYINT NOT NULL COMMENT '预警类型：1课时不足 2有效期不足 3同时命中',
  `follow_status` TINYINT NOT NULL DEFAULT 0 COMMENT '跟进状态：0待跟进 1已跟进 2已续费 3已忽略',
  `first_warning_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次预警时间',
  `last_warning_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近预警时间',
  `closed_at` DATETIME DEFAULT NULL COMMENT '关闭时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `follow_remark` VARCHAR(500) DEFAULT NULL COMMENT '跟进备注', -- 【补充】
  PRIMARY KEY (`id`),
  KEY `idx_pt_renewal_warning_record_member_id` (`member_id`),
  KEY `idx_pt_renewal_warning_record_benefit_id` (`benefit_id`),
  KEY `idx_pt_renewal_warning_record_coach_id` (`coach_id`),
  KEY `idx_pt_renewal_warning_record_store_id` (`store_id`),
  KEY `idx_pt_renewal_warning_record_follow_status` (`follow_status`),
  KEY `idx_renewal_warn_benefit_open` (`benefit_id`, `closed_at`) -- 【补充】
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='续费预警记录表';

CREATE TABLE `pt_cross_store_settlement_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `cross_store_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用跨店结算：0否 1是',
  `income_owner_type` TINYINT NOT NULL COMMENT '收入归属方式：1购买门店 2上课门店 3按比例分成',
  `buy_store_ratio` DECIMAL(5,2) NOT NULL DEFAULT 100.00 COMMENT '购买门店分成比例',
  `lesson_store_ratio` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '上课门店分成比例',
  `coach_fee_owner_type` TINYINT NOT NULL DEFAULT 1 COMMENT '教练课时费归属：1授课教练',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '规则状态：1启用 0停用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_cross_store_settlement_rule_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='跨店结算规则表';

CREATE TABLE `pt_exception_warning_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `warning_type` TINYINT NOT NULL COMMENT '预警类型：1频繁取消预约 2课时消耗异常',
  `period_days` INT NOT NULL COMMENT '统计周期天数',
  `trigger_threshold` INT NOT NULL COMMENT '触发阈值',
  `applicable_store_ids` JSON DEFAULT NULL COMMENT '适用门店ID列表，NULL表示全部门店',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '规则状态：1启用 0停用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_exception_warning_rule_type` (`warning_type`),
  KEY `idx_pt_exception_warning_rule_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='异常预警规则表';

CREATE TABLE `pt_exception_warning_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_id` BIGINT UNSIGNED NOT NULL COMMENT '规则ID',
  `warning_type` TINYINT NOT NULL COMMENT '预警类型：1频繁取消预约 2课时消耗异常',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `coach_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联教练ID',
  `store_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属门店ID',
  `period_start` DATE NOT NULL COMMENT '统计周期开始日期',
  `period_end` DATE NOT NULL COMMENT '统计周期结束日期',
  `trigger_value` INT NOT NULL COMMENT '实际触发值',
  `trigger_desc` VARCHAR(255) NOT NULL COMMENT '触发说明，例如7天内取消4次',
  `warning_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预警时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_exception_warning_record_rule_id` (`rule_id`),
  KEY `idx_pt_exception_warning_record_type` (`warning_type`),
  KEY `idx_pt_exception_warning_record_member_id` (`member_id`),
  KEY `idx_pt_exception_warning_record_coach_id` (`coach_id`),
  KEY `idx_pt_exception_warning_record_store_id` (`store_id`),
  KEY `idx_pt_exception_warning_record_period` (`period_start`, `period_end`),
  UNIQUE KEY `uk_exc_warn_dedup` (`rule_id`, `member_id`, `warning_type`, `period_start`, `period_end`) -- 【补充】
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='异常预警记录表';

CREATE TABLE `pt_group_to_private_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `attendance_days` INT DEFAULT NULL COMMENT '出勤统计周期天数',
  `attendance_threshold` INT DEFAULT NULL COMMENT '出勤次数阈值',
  `purchase_days` INT DEFAULT NULL COMMENT '购课统计周期天数',
  `purchase_threshold` INT DEFAULT NULL COMMENT '购课次数阈值',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_group_to_private_rule_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团课转私教识别规则表';

CREATE TABLE `pt_group_to_private_lead` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `store_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属门店ID',
  `rule_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '命中规则ID',
  `attendance_count` INT NOT NULL DEFAULT 0 COMMENT '团课出勤次数',
  `purchase_count` INT NOT NULL DEFAULT 0 COMMENT '团课购课次数',
  `intention_reason` VARCHAR(255) DEFAULT NULL COMMENT '高意向原因',
  `experience_coupon_status` TINYINT NOT NULL DEFAULT 0 COMMENT '体验券状态：0未发放 1已发放 2已使用',
  `follow_status` TINYINT NOT NULL DEFAULT 0 COMMENT '跟进状态：0待跟进 1已跟进 2已转化 3已放弃',
  `follow_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '跟进人ID',
  `last_follow_time` DATETIME DEFAULT NULL COMMENT '最近跟进时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_group_to_private_lead_member_id` (`member_id`),
  KEY `idx_pt_group_to_private_lead_store_id` (`store_id`),
  KEY `idx_pt_group_to_private_lead_follow_status` (`follow_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团课转私教转化名单表';

CREATE TABLE `pt_group_to_private_follow` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `lead_id` BIGINT UNSIGNED NOT NULL COMMENT '转化名单ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `follow_status` TINYINT NOT NULL COMMENT '跟进状态：0待跟进 1已跟进 2已转化 3已放弃',
  `follow_remark` VARCHAR(500) DEFAULT NULL COMMENT '跟进备注',
  `operator_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '操作人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_group_to_private_follow_lead_id` (`lead_id`),
  KEY `idx_pt_group_to_private_follow_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团课转私教跟进记录表';

