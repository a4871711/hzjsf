-- 私教提成改为由分成规则类型驱动：支付结销售提成，核销结课时费。
ALTER TABLE `pt_private_order`
  ADD COLUMN `coach_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '销售归属教练快照；商品仅指定一名教练时记录' AFTER `store_id`;
