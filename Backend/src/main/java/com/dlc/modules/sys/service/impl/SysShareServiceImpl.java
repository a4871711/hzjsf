package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.service.SysShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.dlc.modules.sys.dao.SysShareDao;
import com.dlc.modules.sys.entity.SysShareEntity;
import java.util.List;

@Service("shareService")
public class SysShareServiceImpl  implements SysShareService {

    @Autowired
    private SysShareDao sysShareDao;

    @Override
    public SysShareEntity queryObject(Long id) {
        return sysShareDao.queryObject(id);
    }

    @Override
    public List<SysShareEntity> queryList(Map<String, Object> map) {
        return sysShareDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysShareDao.queryTotal(map);
    }

    @Override
    public void save(SysShareEntity sysShareEntity) {
        sysShareDao.save(sysShareEntity);
    }

    @Override
    public void update(SysShareEntity sysShareEntity) {
        sysShareDao.update(sysShareEntity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysShareDao.deleteBatch(ids);
    }

  
}
