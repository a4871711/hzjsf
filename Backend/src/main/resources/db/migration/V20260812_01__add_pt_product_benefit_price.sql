-- 私教商品按 VIP 权益卡配置专属权益价；一个商品可配置多张权益卡，每张卡一个价格。
CREATE TABLE IF NOT EXISTS `pt_product_benefit_price` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT UNSIGNED NOT NULL COMMENT '私教商品ID',
  `vip_card_id` BIGINT NOT NULL COMMENT 'VIP权益卡商品ID',
  `benefit_price` DECIMAL(10,2) NOT NULL COMMENT '持有该权益卡时的私教商品权益价',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_product_benefit_price_product_card` (`product_id`, `vip_card_id`),
  KEY `idx_pt_product_benefit_price_vip_card` (`vip_card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私教商品VIP权益价配置';

-- 下单时记录实际命中的权益卡和权益价，后续改价不影响历史订单对账。
ALTER TABLE `pt_private_order`
  ADD COLUMN `benefit_vip_card_id` BIGINT DEFAULT NULL COMMENT '命中的VIP权益卡ID快照' AFTER `validity_days`,
  ADD COLUMN `benefit_price` DECIMAL(10,2) DEFAULT NULL COMMENT '命中的权益价快照' AFTER `benefit_vip_card_id`;
