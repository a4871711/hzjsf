package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.service.SysStoreCoachEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.dlc.modules.sys.dao.SysStoreCoachEvaluateDao;
import com.dlc.modules.sys.entity.SysStoreCoachEvaluateEntity;
import java.util.List;

@Service
public class SysStoreCoachEvaluateServiceImpl  implements SysStoreCoachEvaluateService {

    @Autowired
    private SysStoreCoachEvaluateDao sysStoreCoachEvaluateDao;

    @Override
    public SysStoreCoachEvaluateEntity queryObject(Long id) {
        return sysStoreCoachEvaluateDao.queryObject(id);
    }

    @Override
    public List<SysStoreCoachEvaluateEntity> queryList(Map<String, Object> map) {
        return sysStoreCoachEvaluateDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysStoreCoachEvaluateDao.queryTotal(map);
    }

    @Override
    public int save(SysStoreCoachEvaluateEntity sysStoreCoachEvaluateEntity) {
        return sysStoreCoachEvaluateDao.save(sysStoreCoachEvaluateEntity);
    }

    @Override
    public int update(SysStoreCoachEvaluateEntity sysStoreCoachEvaluateEntity) {
        return  sysStoreCoachEvaluateDao.update(sysStoreCoachEvaluateEntity);
    }

    @Override
    public int deleteBatch(Long[] ids) {
        return sysStoreCoachEvaluateDao.deleteBatch(ids);
    }

  
}
