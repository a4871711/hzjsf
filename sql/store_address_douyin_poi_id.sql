-- 门店地址表：抖音核销 poi_id（美团仍用 goodsIdStoreId）
ALTER TABLE `store_address`
    ADD COLUMN `douyin_poi_id` VARCHAR(64) NULL DEFAULT NULL COMMENT '抖音核销门店poi_id' AFTER `goodsIdStoreId`;
