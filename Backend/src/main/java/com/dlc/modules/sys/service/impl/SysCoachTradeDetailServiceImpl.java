package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.service.SysCoachTradeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.dlc.modules.sys.dao.SysCoachTradeDetailDao;
import com.dlc.modules.sys.entity.SysCoachTradeDetailEntity;
import java.util.List;

@Service("coachTradeDetailService")
public class SysCoachTradeDetailServiceImpl  implements SysCoachTradeDetailService {

    @Autowired
    private SysCoachTradeDetailDao sysCoachTradeDetailDao;

    @Override
    public SysCoachTradeDetailEntity queryObject(Long id) {
        return sysCoachTradeDetailDao.queryObject(id);
    }

    @Override
    public List<SysCoachTradeDetailEntity> queryList(Map<String, Object> map) {
        return sysCoachTradeDetailDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysCoachTradeDetailDao.queryTotal(map);
    }

    @Override
    public void save(SysCoachTradeDetailEntity sysCoachTradeDetailEntity) {
        sysCoachTradeDetailDao.save(sysCoachTradeDetailEntity);
    }

    @Override
    public void update(SysCoachTradeDetailEntity sysCoachTradeDetailEntity) {
        sysCoachTradeDetailDao.update(sysCoachTradeDetailEntity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysCoachTradeDetailDao.deleteBatch(ids);
    }

  
}
