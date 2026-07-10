-- 入场校验热路径(DeviceMapper 三处 excludePausingDevice 的 NOT EXISTS 子查询,
-- 以及 CardPauseRecordMapper.countActivePauseByUser)按 card_order_id / status 过滤,
-- card_pause_record 此前无 card_order_id 索引,停卡记录增多后每次刷脸/扫码开门都全表扫描
CREATE INDEX idx_card_order_status ON card_pause_record (card_order_id, status);
