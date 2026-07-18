-- =====================================================================
-- 私教 · 商品域建表(类型/商品/门店/教练/分期规则/附赠团课)
-- pt_product_type(+4类初始化), pt_product(+推荐位列), 关联表, pt_installment_rule(分期规则建表归此), 附赠团课规则与会员权益
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

CREATE TABLE `pt_product_type` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_name` VARCHAR(50) NOT NULL COMMENT '类型名称，例如体验服务、节次套餐、包月服务、团课',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_product_type_name_deleted` (`type_name`, `deleted`),
  KEY `idx_pt_product_type_status` (`status`),
  KEY `idx_pt_product_type_sort` (`sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品类型表';

INSERT INTO `pt_product_type` (`id`, `type_name`, `sort_no`, `status`) VALUES
(1, '体验服务', 100, 1),
(2, '节次套餐', 90, 1),
(3, '包月服务', 80, 1),
(4, '团课', 70, 1);

CREATE TABLE `pt_product` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_no` VARCHAR(32) NOT NULL COMMENT '商品编号，例如 SJ202606020001',
  `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `product_subtitle` VARCHAR(100) DEFAULT NULL COMMENT '商品副标题',
  `product_type_id` BIGINT UNSIGNED NOT NULL COMMENT '商品类型ID，关联pt_product_type',
  `service_type` TINYINT NOT NULL DEFAULT 1 COMMENT '服务类型：1一对一 2一对多',
  `category_name` VARCHAR(50) DEFAULT NULL COMMENT '商品分类名称',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '商品封面图',
  `product_intro` VARCHAR(255) DEFAULT NULL COMMENT '商品简介',
  `product_detail` MEDIUMTEXT COMMENT '商品详情',
  `target_desc` VARCHAR(255) DEFAULT NULL COMMENT '训练目标说明',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `sale_price` DECIMAL(10,2) NOT NULL COMMENT '售价',
  `member_price` DECIMAL(10,2) DEFAULT NULL COMMENT '会员优惠价',
  `new_user_price` DECIMAL(10,2) DEFAULT NULL COMMENT '新人价',
  `lesson_count` INT NOT NULL COMMENT '课时数量',
  `duration_minutes` INT NOT NULL COMMENT '单次服务时长（分钟）',
  `validity_days` INT NOT NULL COMMENT '有效期天数；长期可约定为-1',
  `refund_type` TINYINT NOT NULL DEFAULT 2 COMMENT '退款方式：1支持 2不支持 3人工审核',
  `refund_rule` VARCHAR(500) DEFAULT NULL COMMENT '退款规则说明',
  `visible_groups` TEXT DEFAULT NULL COMMENT '可见人群，例如 member,new_user,student',
  `purchase_limit` INT DEFAULT NULL COMMENT '每人限购数量',
  `sale_stock` INT DEFAULT NULL COMMENT '可售库存，NULL表示不限量',
  `sold_count` INT NOT NULL DEFAULT 0 COMMENT '已售数量',
  `booking_gap_minutes` INT NOT NULL DEFAULT 0 COMMENT '预约间隔（分钟）',
  `booking_capacity` INT NOT NULL DEFAULT 1 COMMENT '单时段可预约人数',
  `latest_booking_hours` INT NOT NULL DEFAULT 2 COMMENT '最晚可预约，单位小时',
  `latest_free_cancel_hours` INT NOT NULL DEFAULT 2 COMMENT '最晚无责取消，单位小时',
  `no_show_deduct` TINYINT NOT NULL DEFAULT 1 COMMENT '爽约是否扣课：0否 1是',
  `coach_confirm_required` TINYINT NOT NULL DEFAULT 0 COMMENT '预约是否需教练确认：0否 1是',
  `listing_status` TINYINT NOT NULL DEFAULT 0 COMMENT '上架状态：0未上架 1已上架',
  `listing_at` DATETIME DEFAULT NULL COMMENT '上架时间',
  `unlisting_at` DATETIME DEFAULT NULL COMMENT '下架时间',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  `recommend_private` TINYINT NOT NULL DEFAULT 0 COMMENT '是否私教入口推荐:0否 1是', -- 【补充】
  `recommend_home` TINYINT NOT NULL DEFAULT 0 COMMENT '是否首页推荐:0否 1是', -- 【补充】
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_product_no` (`product_no`),
  KEY `idx_pt_product_name` (`product_name`),
  KEY `idx_pt_product_type_id` (`product_type_id`),
  KEY `idx_pt_product_listing_status` (`listing_status`),
  KEY `idx_pt_product_sort` (`sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品主表';

CREATE TABLE `pt_product_store_rel` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  `store_id` BIGINT UNSIGNED NOT NULL COMMENT '门店ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_product_store` (`product_id`, `store_id`),
  KEY `idx_pt_product_store_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品适用门店关联表';

CREATE TABLE `pt_product_coach_rel` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  `coach_id` BIGINT UNSIGNED NOT NULL COMMENT '教练ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_product_coach` (`product_id`, `coach_id`),
  KEY `idx_pt_product_coach_coach_id` (`coach_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品指定教练关联表';

CREATE TABLE `pt_installment_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '私教商品ID',
  `is_enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否支持分期：0否 1是',
  `down_payment_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '首付金额',
  `installment_count` INT DEFAULT NULL COMMENT '分期期数',
  `installment_interval_months` INT DEFAULT NULL COMMENT '每期间隔月数',
  `overdue_pause_booking` TINYINT NOT NULL DEFAULT 1 COMMENT '逾期是否暂停预约：0否 1是',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_installment_rule_product_id` (`product_id`),
  KEY `idx_pt_installment_rule_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品分期规则表';

CREATE TABLE `pt_product_group_benefit` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '私教商品ID',
  `is_enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否赠送团课权益：0否 1是',
  `gift_count` INT DEFAULT NULL COMMENT '赠送团课次数',
  `validity_days` INT DEFAULT NULL COMMENT '团课权益有效期天数',
  `scope_type` TINYINT NOT NULL DEFAULT 1 COMMENT '适用范围：1全部团课 2指定团课商品',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_product_group_benefit_product_id` (`product_id`),
  KEY `idx_pt_product_group_benefit_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品附赠团课权益规则表';

CREATE TABLE `pt_product_group_benefit_rel` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `benefit_id` BIGINT UNSIGNED NOT NULL COMMENT '附赠权益规则ID',
  `group_product_id` BIGINT UNSIGNED NOT NULL COMMENT '团课商品ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_product_group_benefit_rel` (`benefit_id`, `group_product_id`),
  KEY `idx_pt_product_group_benefit_rel_group_product_id` (`group_product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品附赠团课指定商品关联表';

CREATE TABLE `pt_member_group_benefit` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `source_order_id` BIGINT UNSIGNED NOT NULL COMMENT '来源私教订单ID',
  `source_product_id` BIGINT UNSIGNED NOT NULL COMMENT '来源私教商品ID',
  `gift_count` INT NOT NULL COMMENT '赠送总次数',
  `used_count` INT NOT NULL DEFAULT 0 COMMENT '已使用次数',
  `remaining_count` INT NOT NULL DEFAULT 0 COMMENT '剩余次数',
  `effective_time` DATETIME NOT NULL COMMENT '生效时间',
  `expire_time` DATETIME NOT NULL COMMENT '到期时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1生效中 2已用完 3已过期 4已回收',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_member_group_benefit_member_id` (`member_id`),
  KEY `idx_pt_member_group_benefit_source_order_id` (`source_order_id`),
  KEY `idx_pt_member_group_benefit_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员附赠团课权益表';

CREATE TABLE `pt_member_group_benefit_flow` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `benefit_id` BIGINT UNSIGNED NOT NULL COMMENT '会员附赠团课权益ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `flow_type` TINYINT NOT NULL COMMENT '流水类型：1发放 2使用 3回收 4过期',
  `change_count` INT NOT NULL COMMENT '变动次数',
  `before_count` INT NOT NULL COMMENT '变动前次数',
  `after_count` INT NOT NULL COMMENT '变动后次数',
  `biz_type` TINYINT DEFAULT NULL COMMENT '关联业务类型：1私教订单 2团课预约 3退款',
  `biz_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联业务ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_pt_member_group_benefit_flow_benefit_id` (`benefit_id`),
  KEY `idx_pt_member_group_benefit_flow_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员附赠团课权益流水表';

