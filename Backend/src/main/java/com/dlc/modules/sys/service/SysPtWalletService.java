package com.dlc.modules.sys.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 后台 · 会员储值 Service(第19步资金域,/sys/wallet)。
 * <p>查询走 SysPtWalletDao;资金动作(后台充值/冻结/解冻)委托 api 侧 PtMemberWalletService,
 * 复用唯一资金入口 changeBalance,后台不另起余额直改口径。</p>
 *
 * @author claude
 */
public interface SysPtWalletService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    Map<String, Object> queryStat(Map<String, Object> params);

    Map<String, Object> queryObject(Long id);

    List<Map<String, Object>> queryFlowList(Map<String, Object> params);

    int queryFlowTotal(Map<String, Object> params);

    /**
     * 后台人工充值(线下收款补录,不走微信,纯账户加钱)。
     * 委托 changeBalance(flowType=1充值, bizType=3人工调整, remark, operatorId=管理员ID)。
     *
     * @param memberId   会员ID(必填)
     * @param amount     充值金额(>0)
     * @param remark     来源备注
     * @param operatorId 操作管理员ID
     */
    void recharge(Long memberId, BigDecimal amount, String remark, Long operatorId);

    /** 冻结账户(status 1→2),返回 false 表示已冻结/不存在,由 controller 提示 */
    boolean freeze(Long memberId);

    /** 解冻账户(status 2→1),返回 false 表示未冻结/不存在,由 controller 提示 */
    boolean unfreeze(Long memberId);
}
