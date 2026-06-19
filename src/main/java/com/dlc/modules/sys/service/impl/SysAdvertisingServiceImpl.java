package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysAdvertisingDao;
import com.dlc.modules.sys.entity.SysAdvertisingEntity;
import com.dlc.modules.sys.service.SysAdvertisingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SysAdvertisingServiceImpl implements SysAdvertisingService {

    @Autowired
    private SysAdvertisingDao sysAdvertisingDao;

    @Override
    public SysAdvertisingEntity queryObject(Long advId) {
        return sysAdvertisingDao.queryObject(advId);
    }

    @Override
    public List<SysAdvertisingEntity> queryList(Map<String, Object> map) {
        return sysAdvertisingDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysAdvertisingDao.queryTotal(map);
    }

    @Override
    public void save(SysAdvertisingEntity sysAdvertisingEntity) {
        sysAdvertisingDao.save(sysAdvertisingEntity);
    }

    @Override
    public void update(SysAdvertisingEntity sysAdvertisingEntity) {
        sysAdvertisingDao.update(sysAdvertisingEntity);
    }

    @Override
    public void deleteBatch(Long[] advIds) {
        sysAdvertisingDao.deleteBatch(advIds);
    }

    @Override
    public List<Map<String, Object>> queryGoodsList() {
        return null;
    }
}
