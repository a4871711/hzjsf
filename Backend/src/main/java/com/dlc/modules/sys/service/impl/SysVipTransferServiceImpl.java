package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysVipTransferDao;
import com.dlc.modules.sys.service.SysVipTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益转让审核工作台后台服务实现(只读列表)。
 *
 * @date 2026-07-02
 */
@Service("sysVipTransferService")
public class SysVipTransferServiceImpl implements SysVipTransferService {

    @Autowired
    private SysVipTransferDao sysVipTransferDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysVipTransferDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysVipTransferDao.queryTotal(params);
    }
}
