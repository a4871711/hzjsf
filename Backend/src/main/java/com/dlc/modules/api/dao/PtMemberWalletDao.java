package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtMemberWalletEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * 会员储值账户 Dao(pt_member_wallet,第19步)。
 * 余额变动统一由 PtMemberWalletServiceImpl.changeBalance 调度:
 * selectByMemberIdForUpdate 锁行 → 算 after → updateBalance → 插流水,禁止绕开。
 * 对应 mapper/api/PtMemberWalletDao.xml(api XML 改动须重启 Tomcat)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtMemberWalletDao {

    /** 按会员查账户(只读,无锁;查余额/前置校验用) */
    PtMemberWalletEntity selectByMemberId(@Param("memberId") Long memberId);

    /** 按会员行锁取账户(资金变动入口,串行化同一账户的并发变动) */
    PtMemberWalletEntity selectByMemberIdForUpdate(@Param("memberId") Long memberId);

    /**
     * 首充建账户:INSERT ... ON DUPLICATE KEY UPDATE id=id,
     * 靠 uk_pt_member_wallet_member_id 保证并发首充只建一行(幂等,余额等列走 DDL 默认值)。
     */
    int insertOnDuplicate(@Param("memberId") Long memberId);

    /**
     * 行锁内更新余额:balance 直接写调用方基于 before 算出的 after(非 SET balance=balance+x,
     * 与流水 before/after 快照同源可审计);累计充值/消费按流水类型增量累加。
     */
    int updateBalance(@Param("id") Long id,
                      @Param("afterBalance") BigDecimal afterBalance,
                      @Param("rechargeInc") BigDecimal rechargeInc,
                      @Param("consumeInc") BigDecimal consumeInc);

    /** 冻结:带状态条件 1→2,命中0行=已冻结/不存在(幂等判定交调用方) */
    int freeze(@Param("id") Long id);

    /** 解冻:带状态条件 2→1,命中0行=未冻结/不存在 */
    int unfreeze(@Param("id") Long id);
}
