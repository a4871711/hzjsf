-- 系统优惠券：抖音团购 sku_id（验券 prepare 返回的 sku.sku_id）
ALTER TABLE `sys_coupon`
  ADD COLUMN `douyin_sku_id` VARCHAR(64) NULL COMMENT '抖音团购sku_id' AFTER `goodsId`;
