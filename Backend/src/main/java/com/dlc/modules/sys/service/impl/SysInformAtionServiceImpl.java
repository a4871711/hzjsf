package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysInformAtionDao;
import com.dlc.modules.sys.entity.SysInformAtionEntity;
import com.dlc.modules.sys.service.SysInformAtionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class SysInformAtionServiceImpl implements SysInformAtionService {
    @Autowired
    private SysInformAtionDao sysInformAtionDao;
    @Override
    public SysInformAtionEntity queryObject(Long informationId) {
        return sysInformAtionDao.queryObject(informationId);
    }

    @Override
    public List<SysInformAtionEntity> queryList(Map<String, Object> map) {
        return sysInformAtionDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysInformAtionDao.queryTotal(map);
    }

    @Override
    public void save(SysInformAtionEntity informationId) {
        sysInformAtionDao.save(informationId);
    }

    @Override
    public void update(SysInformAtionEntity informationId) {
        sysInformAtionDao.update(informationId);
    }

    @Override
    public void delete(Long informationId) {
        sysInformAtionDao.delete(informationId);
    }

    @Override
    public void deleteBatch(Long[] informationIds) {
        sysInformAtionDao.deleteBatch(informationIds);

    }
}
