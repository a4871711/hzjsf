package com.dlc.modules.sys.service.impl;

import com.dlc.modules.api.service.PrivateOrderService;
import com.dlc.modules.sys.dao.SysPrivateOrderDao;
import com.dlc.modules.sys.service.SysPrivateOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 私教购买记录后台 Service 实现。
 * 落在 sys.service.impl 命中事务切面(REQUIRED);refund 委托 api 侧 PrivateOrderService.refund,
 * 后者加入同一事务(REQUIRED 传播),校验失败/渠道受理失败整体回滚。
 * 注意:退款链路涉及 api 包代码与 mapper/api XML,改动须重启 Tomcat;本页三个 sys XML 热刷新免重启。
 */
@Service("sysPrivateOrderService")
public class SysPrivateOrderServiceImpl implements SysPrivateOrderService {

    @Autowired
    private SysPrivateOrderDao sysPrivateOrderDao;
    @Autowired
    private PrivateOrderService privateOrderService;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysPrivateOrderDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysPrivateOrderDao.queryTotal(params);
    }

    @Override
    public Map<String, Object> queryDetail(Long id, String storeIds) {
        Map<String, Object> detail = sysPrivateOrderDao.queryDetail(id, storeIds);
        if (detail != null) {
            detail.put("couponRel", sysPrivateOrderDao.queryCouponRel(id));
        }
        return detail;
    }

    @Override
    public boolean existsInScope(Long id, String storeIds) {
        return sysPrivateOrderDao.countInScope(id, storeIds) > 0;
    }

    @Override
    public void refund(Long orderId, BigDecimal refundAmount, Integer refundLessons, String remark, Long operatorId) {
        privateOrderService.refund(orderId, refundAmount, refundLessons, remark, operatorId);
    }
}
