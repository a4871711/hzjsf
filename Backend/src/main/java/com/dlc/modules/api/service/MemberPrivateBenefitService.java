package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;

/**
 * 会员私教权益课时账本三态机 Service(第11步,M3 交易域地基)。
 * 口径:恒等式 total = used + frozen + remaining 任何路径不可破;
 * 所有变更"锁权益行 → 带条件 UPDATE → 检查影响行数",影响0行=前置不满足,抛对应 CodeAndMsg。
 *
 * @author claude
 */
public interface MemberPrivateBenefitService {

    /**
     * 支付回调建权益(一次性/分期首付同口径)。按 order_id 查重幂等,已存在直接返回既有权益。
     * total=remaining=lessonCount,effective_at=now,expire_at=now+validityDays(-1 长期为 NULL),status=1。
     *
     * @param orderId      来源私教订单ID(幂等键)
     * @param memberId     会员ID
     * @param productId    私教商品ID
     * @param storeId      购买门店ID
     * @param lessonCount  购买课时数(订单快照)
     * @param validityDays 有效期天数(订单快照,-1 长期)
     * @return 新建或已存在的权益
     */
    PtMemberPrivateBenefitEntity activate(Long orderId, Long memberId, Long productId,
                                          Long storeId, Integer lessonCount, Integer validityDays);

    /** 预约冻结:remaining-=n,frozen+=n;须 status=1 且未过期且 remaining>=n */
    void freeze(Long benefitId, int n);

    /** 完成核销:frozen-=n,used+=n;核销后 remaining=0 且 frozen=0 → status=2 已用完(同事务) */
    void finish(Long benefitId, int n);

    /** 取消预约解冻:frozen-=n,remaining+=n */
    void cancel(Long benefitId, int n);

    /** 退款冲减:total-=n,remaining-=n(不动 frozen,不可冲负);冲减后 remaining=0 且 frozen=0 → status=4 已退款 */
    void refundDeduct(Long benefitId, int n);
}
