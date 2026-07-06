package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.service.PtMemberWalletService;
import com.dlc.modules.sys.dao.SysPtWalletDao;
import com.dlc.modules.sys.service.SysPtWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 后台 · 会员储值 Service 实现(第19步资金域)。落在 sys.service.impl,命中事务切面(REQUIRED)。
 * <p>资金动作委托 api 侧 ptMemberWalletService,复用唯一资金入口 changeBalance;查询走 SysPtWalletDao。</p>
 *
 * @author claude
 */
@Service("sysPtWalletService")
public class SysPtWalletServiceImpl implements SysPtWalletService {

    /** 业务类型:3人工调整(后台充值/冲正) */
    private static final int BIZ_MANUAL = 3;
    /** 流水类型:1充值 */
    private static final int FLOW_RECHARGE = 1;

    @Autowired
    private SysPtWalletDao sysPtWalletDao;
    @Autowired
    private PtMemberWalletService ptMemberWalletService;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysPtWalletDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysPtWalletDao.queryTotal(params);
    }

    @Override
    public Map<String, Object> queryStat(Map<String, Object> params) {
        return sysPtWalletDao.queryStat(params);
    }

    @Override
    public Map<String, Object> queryObject(Long id) {
        return sysPtWalletDao.queryObject(id);
    }

    @Override
    public List<Map<String, Object>> queryFlowList(Map<String, Object> params) {
        return sysPtWalletDao.queryFlowList(params);
    }

    @Override
    public int queryFlowTotal(Map<String, Object> params) {
        return sysPtWalletDao.queryFlowTotal(params);
    }

    @Override
    public void recharge(Long memberId, BigDecimal amount, String remark, Long operatorId) {
        if (memberId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RRException("会员ID与充值金额必填,且金额须大于0");
        }
        // 委托唯一资金入口:行锁内加钱+写流水;冻结账户会在 changeBalance 内抛 ERROR_WALLET_FROZEN;
        // 账户不存在则 changeBalance 内首充建户(后台补录首笔充值即建户,幂等)
        ptMemberWalletService.changeBalance(memberId, amount, FLOW_RECHARGE,
                BIZ_MANUAL, null, null, remark, operatorId);
    }

    @Override
    public boolean freeze(Long memberId) {
        return ptMemberWalletService.freeze(memberId);
    }

    @Override
    public boolean unfreeze(Long memberId) {
        return ptMemberWalletService.unfreeze(memberId);
    }
}
