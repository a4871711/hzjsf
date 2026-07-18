-- 秒杀功能 P0：秒杀活动多态化 + 各域订单加秒杀来源列 + 顶级菜单「卡券管理」改名「营销中心」。
-- 依据《秒杀功能.md》§7 落地清单、§2 多态模型。P0 商品池=会员卡/权益卡/私教商品(biz_type 三态)。
-- 现有 mk_flash_sale_activity 的存量数据均为私教商品，故 biz_type DEFAULT 1(私教)不改变旧行语义。

-- 1) 秒杀活动主表多态化：biz_type 区分商品域，product_id 仍为该域下的商品ID；cover_url 为会员端卡片首图。
ALTER TABLE `mk_flash_sale_activity`
  ADD COLUMN `biz_type` TINYINT NOT NULL DEFAULT 1 COMMENT '业务类型：1私教商品 2会员卡 3权益卡' AFTER `activity_name`,
  ADD COLUMN `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '秒杀首图URL(会员端卡片展示)' AFTER `biz_type`,
  ADD KEY `idx_mk_flash_sale_biz` (`biz_type`, `status`, `start_time`, `end_time`);

-- 2) 会员卡订单加秒杀来源列：命中秒杀的会员卡下单在此登记活动ID(用于对账/限购计数/回调 CAS 扣减)。
ALTER TABLE `card_order`
  ADD COLUMN `flash_sale_activity_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '秒杀活动ID(会员卡秒杀来源,mk_flash_sale_activity.id)';

-- 3) 权益卡实例加秒杀来源列(vip_benefit 既是购买占位单又是资产)。
ALTER TABLE `vip_benefit`
  ADD COLUMN `flash_sale_activity_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '秒杀活动ID(权益卡秒杀来源,mk_flash_sale_activity.id)';

-- 4) 顶级目录「卡券管理」改名「营销中心」(仅改 name，url/perms/子菜单不变；重复执行第二次匹配 0 行，幂等)。
UPDATE sys_menu SET `name` = '营销中心' WHERE `name` = '卡券管理' AND parent_id = 0 AND type = 0;
