-- 自动续费重复扣款疑似受影响会员核对清单
-- 用途: 与微信商户平台「委托代扣(PAP)」交易流水/对账单逐条核对,确认是否真的被多扣款
-- 根因: DeviceMapper.xml 的 selectAutoPayUser 缺少"近期已下单不重复选中"去重条件(本地已修复,线上尚未部署)
-- 排查范围: 2026-06-20 至今,autoPay>0 且同一 device/用户短时间内出现多条订单的记录

SELECT
    co.deviceId,
    co.userId,
    ui.phone            AS 手机号,
    ui.nickname         AS 昵称,
    ui.contractId       AS 微信代扣协议id,
    co.cardOrderId       AS 订单ID,
    co.orderNo           AS 系统订单号,
    co.transactionNo     AS 微信交易号_有则真到账,
    co.realPayment        AS 应付金额,
    co.status             AS 订单状态_0待支付_4已完成_5已取消,
    co.createdDate         AS 订单创建时间_对应发起代扣申请的时间点,
    co.buyCount
FROM shilijsf.card_order co
LEFT JOIN shilijsf.user_info ui ON ui.userId = co.userId
WHERE co.autoPay > 0
  AND co.createdDate >= '2026-06-20'
  AND co.deviceId IN (
      -- 与本次投诉(deviceId=8948)同类症状的 device: 记录数 2~3 条、首次创建时间精确落在 08:00:0x(cron 触发点)
      8948,   -- 13790228216, 本次投诉会员
      9939,   -- 手机137663...9180, 06-22 08:00 -> 06-22 22:04 -> 06-24 21:14, 横跨2天
      9953,   -- 手机135109...4567, 06-23 08:00 -> 06-25 20:07, 横跨2天
      9979    -- 手机137134...7808, 06-25 08:00 -> 06-25 12:24, 同天两次
  )
ORDER BY co.deviceId, co.cardOrderId;

-- 核对方法提示:
-- 1. 对每个 deviceId 分组内的多条订单,去微信商户平台按"发起时间"和"金额"逐条查代扣交易流水
-- 2. 重点看 status=5(系统认为"已取消")的那些订单 —— 本次已证实"系统显示取消"不代表微信没扣钱,
--    13790228216 这个案例里两笔都被扣款成功,但系统里连一条完整入账记录都没有
-- 3. 如果微信流水里同一 contractId 在这几个时间点附近有多笔扣款,且系统只有0或1条 income_pay_detail 记录,
--    说明差额部分就是需要退款给会员的金额
