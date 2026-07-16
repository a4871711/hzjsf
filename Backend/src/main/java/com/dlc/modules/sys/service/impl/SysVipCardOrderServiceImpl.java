package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysVipCardOrderDao;
import com.dlc.modules.sys.service.SysVipCardOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡购买记录后台服务实现(只读列表)。
 *
 * @date 2026-07-03
 */
@Service("sysVipCardOrderService")
public class SysVipCardOrderServiceImpl implements SysVipCardOrderService {

    @Autowired
    private SysVipCardOrderDao sysVipCardOrderDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysVipCardOrderDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysVipCardOrderDao.queryTotal(params);
    }

    @Override
    public List<Map<String, Object>> queryExportList(Map<String, Object> params) {
        return sysVipCardOrderDao.queryExportList(params);
    }
}
