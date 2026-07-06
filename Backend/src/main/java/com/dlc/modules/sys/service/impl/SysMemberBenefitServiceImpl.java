package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysMemberBenefitDao;
import com.dlc.modules.sys.service.SysMemberBenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 会员私教权益后台 Service 实现(纯只读透传)。
 */
@Service("sysMemberBenefitService")
public class SysMemberBenefitServiceImpl implements SysMemberBenefitService {

    @Autowired
    private SysMemberBenefitDao sysMemberBenefitDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysMemberBenefitDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysMemberBenefitDao.queryTotal(params);
    }

    @Override
    public Map<String, Object> queryStat(Map<String, Object> params) {
        return sysMemberBenefitDao.queryStat(params);
    }

    @Override
    public Map<String, Object> queryDetail(Long id, String storeIds) {
        return sysMemberBenefitDao.queryDetail(id, storeIds);
    }
}
