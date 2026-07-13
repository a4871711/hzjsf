package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.SysVipMemberDao;
import com.dlc.modules.sys.service.SysVipMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 权益会员后台管理实现。状态流转全部用「带前置状态的条件 UPDATE」保证并发安全:
 * 命中 0 行即当前状态不允许,抛业务异常回传前端提示。
 * status 语义:9待支付 0正常 1已转出 2已冻结 3已过期 4已注销(后台注销专用终态,
 * api 侧有效性判定为 status=0 且未过期,2/4 天然失效)。
 *
 * @date 2026-07-12
 */
@Service("sysVipMemberService")
public class SysVipMemberServiceImpl implements SysVipMemberService {

    @Autowired
    private SysVipMemberDao sysVipMemberDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> map) {
        return sysVipMemberDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysVipMemberDao.queryTotal(map);
    }

    @Override
    public List<Map<String, Object>> queryExportList(Map<String, Object> map) {
        return sysVipMemberDao.queryExportList(map);
    }

    @Override
    public void freeze(Long vipBenefitId, boolean disable) {
        int rows = disable
                ? sysVipMemberDao.updateStatus(vipBenefitId, Arrays.asList(0), 2)
                : sysVipMemberDao.updateStatus(vipBenefitId, Arrays.asList(2), 0);
        if (rows == 0) {
            throw new RRException(disable ? "仅「正常」状态可停用" : "仅「已冻结」状态可启用");
        }
    }

    @Override
    public void updateRemark(Long vipBenefitId, String remark) {
        if (sysVipMemberDao.updateRemark(vipBenefitId, remark) == 0) {
            throw new RRException("权益记录不存在");
        }
    }

    @Override
    public void updateValidity(Long vipBenefitId, String expireTime) {
        // WHERE 带 status IN(0,2) 守卫:命中 0 行 = 记录不存在或处于已转出/已注销/待支付等不可改状态
        if (sysVipMemberDao.updateValidity(vipBenefitId, expireTime) == 0) {
            throw new RRException("该权益当前状态不可改有效期");
        }
    }

    @Override
    public void changeStore(Long vipBenefitId, Long storeAddrId) {
        // JOIN store_address 反查 storeId,门店不存在/记录处于终态同样命中 0 行
        if (sysVipMemberDao.updateStore(vipBenefitId, storeAddrId) == 0) {
            throw new RRException("目标门店不存在或该权益当前状态不可改门店");
        }
    }

    @Override
    public void cancel(Long vipBenefitId) {
        // 有在途转让单(20待审核/40待受让人确认)时禁止注销:否则转让单成幽灵、转让人服务费被占押
        if (sysVipMemberDao.countActiveTransfer(vipBenefitId) > 0) {
            throw new RRException("该权益有进行中的转让,请先处理转让后再注销");
        }
        if (sysVipMemberDao.updateStatus(vipBenefitId, Arrays.asList(0, 2), 4) == 0) {
            throw new RRException("待支付/已转出的权益不能注销");
        }
    }
}
