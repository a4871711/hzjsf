package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.service.SysGoodsEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.dlc.modules.sys.dao.SysGoodsEvaluateDao;
import com.dlc.modules.sys.entity.SysGoodsEvaluateEntity;
import java.util.List;

@Service("goodsEvaluateService")
public class SysGoodsEvaluateServiceImpl  implements SysGoodsEvaluateService {

    @Autowired
    private SysGoodsEvaluateDao sysGoodsEvaluateDao;

    @Override
    public SysGoodsEvaluateEntity queryObject(Long id) {
        return sysGoodsEvaluateDao.queryObject(id);
    }

    @Override
    public List<SysGoodsEvaluateEntity> queryList(Map<String, Object> map) {
        return sysGoodsEvaluateDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysGoodsEvaluateDao.queryTotal(map);
    }

    @Override
    public void save(SysGoodsEvaluateEntity sysGoodsEvaluateEntity) {
        sysGoodsEvaluateDao.save(sysGoodsEvaluateEntity);
    }

    @Override
    public void update(SysGoodsEvaluateEntity sysGoodsEvaluateEntity) {
        sysGoodsEvaluateDao.update(sysGoodsEvaluateEntity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysGoodsEvaluateDao.deleteBatch(ids);
    }

  
}
