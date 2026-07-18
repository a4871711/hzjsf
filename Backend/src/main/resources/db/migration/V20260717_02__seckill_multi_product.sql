-- 秒杀升级(照《秒杀功能.dc.html》设计稿)：一活动多商品 + 单次/循环投放 + 倒计时预热 + 售罄策略。
-- mk_flash_sale_activity 现为空表(0行,V20260717_01 后),故可直接重构:单商品列改可空、不再使用,商品迁入子表。
-- 「不需要美团/抖音」——mt_group_buy_id/dy_group_buy_id 表列保留(避免删列风险)但前端/实体不再暴露。

-- 1) 活动主表:补投放方式/循环周期/生效日/库存方式/倒计时/售罄策略;原单商品列改可空(改由子表承载)
ALTER TABLE `mk_flash_sale_activity`
  ADD COLUMN `delivery_type`       TINYINT      NOT NULL DEFAULT 1  COMMENT '投放方式:1单次生效 2循环生效' AFTER `cover_url`,
  ADD COLUMN `activity_start_date` DATE         DEFAULT NULL        COMMENT '循环生效-活动周期开始日',
  ADD COLUMN `activity_end_date`   DATE         DEFAULT NULL        COMMENT '循环生效-活动周期结束日',
  ADD COLUMN `week_days`           VARCHAR(20)  DEFAULT NULL        COMMENT '循环生效-生效日,CSV 1..7(周一..周日)',
  ADD COLUMN `stock_mode`          TINYINT      NOT NULL DEFAULT 2  COMMENT '库存方式:1每日投放 2投放总量',
  ADD COLUMN `countdown_enabled`   TINYINT      NOT NULL DEFAULT 0  COMMENT '秒杀前倒计时:0关 1开',
  ADD COLUMN `countdown_minutes`   INT          DEFAULT NULL        COMMENT '倒计时预热分钟(1/5/15)',
  ADD COLUMN `sold_out_action`     TINYINT      NOT NULL DEFAULT 1  COMMENT '售罄后:1自动结束恢复原价 2显示售罄待结束';

ALTER TABLE `mk_flash_sale_activity`
  MODIFY COLUMN `product_id`       BIGINT UNSIGNED DEFAULT NULL COMMENT '【弃用,改由 mk_flash_sale_product 承载】',
  MODIFY COLUMN `flash_sale_price` DECIMAL(10,2)   DEFAULT NULL COMMENT '【弃用】',
  MODIFY COLUMN `activity_stock`   INT             DEFAULT NULL COMMENT '【弃用】',
  MODIFY COLUMN `start_time`       DATETIME        DEFAULT NULL COMMENT '单次生效-开始时间',
  MODIFY COLUMN `end_time`         DATETIME        DEFAULT NULL COMMENT '单次生效-结束时间';

-- 2) 商品子表:一活动多商品,每商品独立秒杀价/库存/已售
CREATE TABLE `mk_flash_sale_product` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id`      BIGINT UNSIGNED NOT NULL COMMENT '秒杀活动ID(mk_flash_sale_activity.id)',
  `product_id`      BIGINT UNSIGNED NOT NULL COMMENT '商品ID(含义随活动 biz_type:2会员卡fit_card/3权益卡vip_benefit_card)',
  `flash_sale_price` DECIMAL(10,2)  NOT NULL COMMENT '秒杀价',
  `activity_stock`   INT            NOT NULL COMMENT '秒杀库存(总量方式=总库存;每日方式=每日投放量)',
  `sold_count`       INT            NOT NULL DEFAULT 0 COMMENT '累计已售(总量方式据此判罄;每日方式仅统计,判罄看 daily_sold)',
  `deleted`          TINYINT        NOT NULL DEFAULT 0 COMMENT '是否删除:0否 1是',
  `created_at`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_fsp_activity` (`activity_id`),
  KEY `idx_fsp_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀活动-商品子表';

-- 3) 每日时段子表(循环生效,可多个)
CREATE TABLE `mk_flash_sale_time_slot` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` BIGINT UNSIGNED NOT NULL COMMENT '秒杀活动ID',
  `start_hm`    VARCHAR(5)      NOT NULL COMMENT '每日开始时间 HH:mm',
  `end_hm`      VARCHAR(5)      NOT NULL COMMENT '每日结束时间 HH:mm',
  PRIMARY KEY (`id`),
  KEY `idx_fsts_activity` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀活动-每日投放时段(循环生效)';

-- 4) 每日已售计数(库存方式=每日投放时,按 商品×日期 做 CAS 扣减与幂等)
CREATE TABLE `mk_flash_sale_daily_sold` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` BIGINT UNSIGNED NOT NULL COMMENT '秒杀活动ID',
  `product_id`  BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  `sold_date`   DATE            NOT NULL COMMENT '售卖日期',
  `sold_count`  INT             NOT NULL DEFAULT 0 COMMENT '当日已售',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fsds` (`activity_id`, `product_id`, `sold_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀活动-每日已售计数(每日投放库存用)';
