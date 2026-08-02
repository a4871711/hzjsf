-- 私教提成结算改造。历史商品/订单默认按次，执行前请在变更窗口备份。
ALTER TABLE `pt_product`
  ADD COLUMN `settlement_mode` TINYINT NOT NULL DEFAULT 1 COMMENT '提成结算方式：1按次 2包月' AFTER `validity_days`;

ALTER TABLE `pt_private_order`
  ADD COLUMN `settlement_mode` TINYINT NOT NULL DEFAULT 1 COMMENT '结算方式快照：1按次 2包月' AFTER `store_id`,
  ADD COLUMN `coach_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '包月商品归属教练快照' AFTER `settlement_mode`;
