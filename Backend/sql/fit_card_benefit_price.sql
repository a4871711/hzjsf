-- 会员卡：权益卡价格（权益会员专享价）
-- 权益会员 = 名下有「正常且未过期」VIP权益卡(vip_benefit)的会员。
-- 配置了 benefitPrice(>0) 的会员卡，权益会员在小程序看到并按此价购买；其他会员不受影响。
ALTER TABLE `fit_card`
  ADD COLUMN `benefitPrice` DECIMAL(10,2) NULL DEFAULT NULL COMMENT '权益卡价格（权益会员专享价，NULL/0=不启用）' AFTER `costPrice`;
