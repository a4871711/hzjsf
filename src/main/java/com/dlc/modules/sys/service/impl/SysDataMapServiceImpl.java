package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysDataMapDao;
import com.dlc.modules.sys.entity.SysDataMapEntity;
import com.dlc.modules.sys.service.SysDataMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SysDataMapServiceImpl implements SysDataMapService {
    @Autowired
    private SysDataMapDao sysDataMapDao;

    @Override
    public List<SysDataMapEntity> queryList(Map<String, Object> map) {
        return sysDataMapDao.queryList(map);
    }

    @Override
    public SysDataMapEntity queryObject(Long dataMapId) {
        return sysDataMapDao.queryObject(dataMapId);
    }

    @Override
    public int save(SysDataMapEntity dataMapEntity) {
        return sysDataMapDao.save(dataMapEntity);
    }

    @Override
    public int update(SysDataMapEntity dataMapEntity) {
        return sysDataMapDao.update(dataMapEntity);
    }



    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysDataMapDao.queryTotal(map);
    }

    @Override
    public void delete(Long datamapId) {
        sysDataMapDao.delete(datamapId);
    }

    @Override
    public void deleteBatch(Long[] datamapIds) {
        sysDataMapDao.deleteBatch(datamapIds);
    }



}
