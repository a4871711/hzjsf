-- VIP 权益卡「生效/到期日期」按会员卡剩余有效期顺延
-- 下单时把会员卡剩余整天数快照到 defer_days,激活时 start=now+defer_days、expire=now+(defer_days+validity_days)。
-- 规则:无会员有效期 → defer_days=0(立即生效);有 N 天 → 顺延 N 天;加购按「本单会员卡之前」的剩余天数。
ALTER TABLE vip_benefit
  ADD COLUMN defer_days INT NOT NULL DEFAULT 0
  COMMENT '下单时会员卡剩余整天数快照,激活据此顺延生效/到期'
  AFTER expire_time;
