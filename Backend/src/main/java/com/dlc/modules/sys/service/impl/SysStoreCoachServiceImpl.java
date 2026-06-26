package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.service.SysStoreCoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.dlc.modules.sys.dao.SysStoreCoachDao;
import com.dlc.modules.sys.entity.SysStoreCoachEntity;
import java.util.List;

@Service
public class SysStoreCoachServiceImpl  implements SysStoreCoachService {

    @Autowired
    private SysStoreCoachDao sysStoreCoachDao;

    @Override
    public SysStoreCoachEntity queryObject(Long id) {
        return sysStoreCoachDao.queryObject(id);
    }

    @Override
    public List<SysStoreCoachEntity> queryList(Map<String, Object> map) {
        return sysStoreCoachDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysStoreCoachDao.queryTotal(map);
    }

    @Override
    public int save(SysStoreCoachEntity sysStoreCoachEntity) {
        return sysStoreCoachDao.save(sysStoreCoachEntity);
    }

    @Override
    public int update(SysStoreCoachEntity sysStoreCoachEntity) {
        return  sysStoreCoachDao.update(sysStoreCoachEntity);
    }

    @Override
    public int deleteBatch(Long[] ids) {
        return sysStoreCoachDao.deleteBatch(ids);
    }

  
}
