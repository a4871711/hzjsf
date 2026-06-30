-- =====================================================================
-- 私教 · 营销域建表(优惠券/拼团/秒杀/会员领券)
-- mk_coupon, 券门店/商品关联, mk_member_coupon(+联合索引), mk_group_buy_activity, mk_flash_sale_activity
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

CREATE TABLE `mk_coupon` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coupon_name` VARCHAR(100) NOT NULL COMMENT '券名称',
  `coupon_type` TINYINT NOT NULL COMMENT '券类型：1满减券 2折扣券',
  `mt_group_buy_id` VARCHAR(64) DEFAULT NULL COMMENT '美团团购ID',
  `dy_group_buy_id` VARCHAR(64) DEFAULT NULL COMMENT '抖音团购ID',
  `discount_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '优惠金额，满减券使用',
  `discount_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '折扣值，例如8.50表示8.5折',
  `max_discount_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '最高优惠金额，非必填',
  `use_threshold_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '使用门槛金额',
  `valid_days` INT NOT NULL COMMENT '有效天数',
  `scope_type` TINYINT NOT NULL DEFAULT 1 COMMENT '适用范围：1全部商品 2指定商品',
  `is_new_user_coupon` TINYINT NOT NULL DEFAULT 0 COMMENT '是否新人券：0否 1是',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_mk_coupon_name` (`coupon_name`),
  KEY `idx_mk_coupon_type` (`coupon_type`),
  KEY `idx_mk_coupon_status` (`status`),
  KEY `idx_mk_coupon_scope_type` (`scope_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券主表';

CREATE TABLE `mk_coupon_store_rel` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coupon_id` BIGINT UNSIGNED NOT NULL COMMENT '优惠券ID',
  `store_id` BIGINT UNSIGNED NOT NULL COMMENT '门店ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mk_coupon_store` (`coupon_id`, `store_id`),
  KEY `idx_mk_coupon_store_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券可用门店关联表';

CREATE TABLE `mk_coupon_product_rel` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coupon_id` BIGINT UNSIGNED NOT NULL COMMENT '优惠券ID',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mk_coupon_product` (`coupon_id`, `product_id`),
  KEY `idx_mk_coupon_product_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券指定商品关联表';

CREATE TABLE `mk_member_coupon` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `coupon_id` BIGINT UNSIGNED NOT NULL COMMENT '优惠券ID',
  `coupon_name` VARCHAR(100) NOT NULL COMMENT '优惠券名称快照',
  `coupon_type` TINYINT NOT NULL COMMENT '券类型：1满减券 2折扣券',
  `receive_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `expire_time` DATETIME NOT NULL COMMENT '到期时间',
  `use_status` TINYINT NOT NULL DEFAULT 0 COMMENT '使用状态：0未使用 1已使用 2已过期',
  `used_order_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '使用订单ID',
  `used_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mk_member_coupon_member_id` (`member_id`),
  KEY `idx_mk_member_coupon_coupon_id` (`coupon_id`),
  KEY `idx_mk_member_coupon_status` (`use_status`),
  KEY `idx_mk_member_coupon_expire_time` (`expire_time`),
  KEY `idx_mk_member_coupon_member_status` (`member_id`, `use_status`, `expire_time`) -- 【补充】
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员优惠券领取记录表';

CREATE TABLE `mk_group_buy_activity` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_name` VARCHAR(100) NOT NULL COMMENT '拼团活动名称',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '关联商品ID',
  `group_buy_price` DECIMAL(10,2) NOT NULL COMMENT '拼团价',
  `group_member_count` INT NOT NULL COMMENT '成团人数',
  `activity_stock` INT DEFAULT NULL COMMENT '活动库存，NULL表示不限量',
  `sold_count` INT NOT NULL DEFAULT 0 COMMENT '已售数量',
  `purchase_limit` INT DEFAULT NULL COMMENT '每人限购数量，NULL表示不限购',
  `start_time` DATETIME NOT NULL COMMENT '活动开始时间',
  `end_time` DATETIME NOT NULL COMMENT '活动结束时间',
  `mt_group_buy_id` VARCHAR(64) DEFAULT NULL COMMENT '美团团购ID',
  `dy_group_buy_id` VARCHAR(64) DEFAULT NULL COMMENT '抖音团购ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_mk_group_buy_name` (`activity_name`),
  KEY `idx_mk_group_buy_product_id` (`product_id`),
  KEY `idx_mk_group_buy_status` (`status`),
  KEY `idx_mk_group_buy_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拼团活动主表';

CREATE TABLE `mk_flash_sale_activity` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_name` VARCHAR(100) NOT NULL COMMENT '秒杀活动名称',
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '关联商品ID',
  `flash_sale_price` DECIMAL(10,2) NOT NULL COMMENT '秒杀价',
  `activity_stock` INT NOT NULL COMMENT '秒杀库存',
  `sold_count` INT NOT NULL DEFAULT 0 COMMENT '已售数量',
  `purchase_limit` INT DEFAULT NULL COMMENT '每人限购数量，NULL表示不限购',
  `start_time` DATETIME NOT NULL COMMENT '活动开始时间',
  `end_time` DATETIME NOT NULL COMMENT '活动结束时间',
  `mt_group_buy_id` VARCHAR(64) DEFAULT NULL COMMENT '美团团购ID',
  `dy_group_buy_id` VARCHAR(64) DEFAULT NULL COMMENT '抖音团购ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_mk_flash_sale_name` (`activity_name`),
  KEY `idx_mk_flash_sale_product_id` (`product_id`),
  KEY `idx_mk_flash_sale_status` (`status`),
  KEY `idx_mk_flash_sale_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='限时秒杀活动主表';

