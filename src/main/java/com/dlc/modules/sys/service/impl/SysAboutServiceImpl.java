package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysAboutDao;
import com.dlc.modules.sys.entity.SysAboutEntity;
import com.dlc.modules.sys.service.SysAboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class SysAboutServiceImpl implements SysAboutService {
    @Autowired
    private SysAboutDao sysAboutDao;
    @Override
    public SysAboutEntity queryObject(Long id) {
        return sysAboutDao.queryObject(id);
    }

    @Override
    public List<SysAboutEntity> queryList(Map<String, Object> map) {
        return sysAboutDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysAboutDao.queryTotal(map);
    }

    @Override
    public void save(SysAboutEntity entity) {
        sysAboutDao.save(entity);
    }

    @Override
    public void update(SysAboutEntity entity) {
        sysAboutDao.update(entity);
    }

    @Override
    public void delete(Long id) {
        sysAboutDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysAboutDao.deleteBatch(ids);
    }
}
