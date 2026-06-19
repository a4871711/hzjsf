package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.service.SysSystemMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.dlc.modules.sys.dao.SysSystemMsgDao;
import com.dlc.modules.sys.entity.SysSystemMsgEntity;
import java.util.List;

@Service("systemMsgService")
public class SysSystemMsgServiceImpl  implements SysSystemMsgService {

    @Autowired
    private SysSystemMsgDao sysSystemMsgDao;

    @Override
    public SysSystemMsgEntity queryObject(Long id) {
        return sysSystemMsgDao.queryObject(id);
    }

    @Override
    public List<SysSystemMsgEntity> queryList(Map<String, Object> map) {
        return sysSystemMsgDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysSystemMsgDao.queryTotal(map);
    }

    @Override
    public void save(SysSystemMsgEntity sysSystemMsgEntity) {
        sysSystemMsgDao.save(sysSystemMsgEntity);
    }

    @Override
    public void update(SysSystemMsgEntity sysSystemMsgEntity) {
        sysSystemMsgDao.update(sysSystemMsgEntity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysSystemMsgDao.deleteBatch(ids);
    }

  
}
