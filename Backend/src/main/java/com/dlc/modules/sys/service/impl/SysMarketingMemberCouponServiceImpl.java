package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.MkMemberCouponDao;
import com.dlc.modules.sys.entity.MkMemberCouponEntity;
import com.dlc.modules.sys.service.SysMarketingMemberCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 会员领券记录 Service 实现（只读）。列表联券名快照/会员信息/使用订单号，
 * 记录由后台发券（grant）与会员端领券（第18步）生成，后台不提供增删改。
 *
 * @author claude
 */
@Service("sysMarketingMemberCouponService")
public class SysMarketingMemberCouponServiceImpl implements SysMarketingMemberCouponService {

    @Autowired
    private MkMemberCouponDao mkMemberCouponDao;

    @Override
    public MkMemberCouponEntity queryObject(Long id) {
        return mkMemberCouponDao.queryObject(id);
    }

    @Override
    public List<MkMemberCouponEntity> queryList(Map<String, Object> map) {
        return mkMemberCouponDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return mkMemberCouponDao.queryTotal(map);
    }
}
