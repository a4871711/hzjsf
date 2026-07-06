package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 会员私教权益课时账本 Dao(pt_member_private_benefit)。
 * 所有课时变更走"selectForUpdate 锁行 → 带条件原子 UPDATE → 检查影响行数",影响0行=前置不满足。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtMemberPrivateBenefitDao {

    PtMemberPrivateBenefitEntity queryObject(Long id);

    /** 按主键行锁查权益行(串行化同一权益的并发课时变更) */
    PtMemberPrivateBenefitEntity selectForUpdate(@Param("id") Long id);

    /** 按来源订单查权益(一单一权益,activate 幂等查重用) */
    PtMemberPrivateBenefitEntity selectByOrderId(@Param("orderId") Long orderId);

    /** 新建权益(回填自增 id;benefit_no 撞唯一键由调用方重试) */
    int save(PtMemberPrivateBenefitEntity entity);

    /** 冻结:remaining-=n,frozen+=n;条件 status=1 且 remaining>=n */
    int freezeLessons(@Param("id") Long id, @Param("n") int n);

    /** 核销:frozen-=n,used+=n;条件 frozen>=n */
    int finishLessons(@Param("id") Long id, @Param("n") int n);

    /** 取消解冻:frozen-=n,remaining+=n;条件 frozen>=n */
    int cancelLessons(@Param("id") Long id, @Param("n") int n);

    /** 退款冲减:total-=n,remaining-=n(同步减 total 保恒等式,不动 frozen);条件 remaining>=n */
    int refundDeductLessons(@Param("id") Long id, @Param("n") int n);

    /** 核销后收口:remaining=0 且 frozen=0 时 1生效中→2已用完 */
    int markUsedUp(@Param("id") Long id);

    /** 退款冲减后收口:remaining=0 且 frozen=0 时置 4已退款 */
    int markRefunded(@Param("id") Long id);

    /** 商品被权益引用计数(全状态,权益是会员资产须可回溯);>0 商品不可删除(第14步回填第9步护栏) */
    int countByProduct(@Param("productId") Long productId);

    /**
     * 批量过期(第16步定时任务):status=1 且 expire_at 已过 且 frozen_lessons=0 → 置 3已过期。
     * frozen=0 条件天然跳过有进行中预约的权益(待 finish/cancel 解冻后下轮收口);
     * 单条 UPDATE 自带幂等,返回影响行数供日志。
     */
    int expireBatch();
}
