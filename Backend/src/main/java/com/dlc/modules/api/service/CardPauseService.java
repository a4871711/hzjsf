package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 会员自助停卡 Service(移动端)
 * 定期停卡:免费申请成功、付费支付成功后均从次日0点开始，并预顺延有效期;
 * 开始前可取消并扣回全部顺延天数，开始后提前恢复按未用天数扣回顺延(付费不退款)。
 */
public interface CardPauseService {

    /** 停卡预检:免费额度是否可用/下次可用时间/付费档位列表。cardOrderId 非空时额外校验该卡是否权益卡性质(申请弹层前置校验用) */
    Map<String, Object> precheck(Long userId, Long cardOrderId);

    /** 申请停卡:免费(pauseType=0)次日0点生效;付费(pauseType=1)建待支付单返回支付信息 */
    Map<String, Object> apply(Long userId, Long cardOrderId, Integer pauseType, Integer pauseDays, Integer tierIndex);

    /** 付费停卡支付回调:置生效+顺延有效期+记账,幂等;返回1首次生效成功 */
    int payCallback(String orderNo, BigDecimal money, String transactionId, Integer payType);

    /** 提前取消停卡:按未使用天数扣回顺延(付费不退款);存量开放式记录走旧恢复语义;返回1首次取消成功 */
    int cancel(Long userId, Long pauseId);

    /** 我的停卡记录分页 */
    PageUtils myList(Map<String, Object> params);
}
