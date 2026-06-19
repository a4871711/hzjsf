package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysDataMapDao;
import com.dlc.modules.sys.dao.SysGradePriceShipDao;
import com.dlc.modules.sys.entity.SysGradePriceShipEntity;
import com.dlc.modules.sys.service.SysGradePriceShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Service
public class SysGradePriceShipServiceImpl implements SysGradePriceShipService {
    @Autowired
    private SysGradePriceShipDao sysLevelPriceShipDao;
    @Autowired
    private SysDataMapDao sysDataMapDao;
    @Override
    public List<SysGradePriceShipEntity> queryList(Map<String, Object> map) {
        return sysLevelPriceShipDao.queryList(map);
    }

    @Override
    public SysGradePriceShipEntity queryObject(Long id) {
        return sysLevelPriceShipDao.queryObject(id);
    }

    @Override
    public int save(SysGradePriceShipEntity entity) {
        return sysLevelPriceShipDao.save(entity);
    }

    @Override
    public int update(SysGradePriceShipEntity entity) {

        return sysLevelPriceShipDao.update(entity);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysLevelPriceShipDao.queryTotal(map);
    }

    @Override
    public void delete(Long id) {
        sysLevelPriceShipDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysLevelPriceShipDao.deleteBatch(ids);
    }
    @Override
    public void savap(Long entityId,Long dengji,BigDecimal price) {
        sysLevelPriceShipDao.savap(entityId,dengji,price);
    }

    @Override
    public Integer getGradeRule(Double money) {
        Integer grade = sysDataMapDao.getGradeRule(money);
        if (grade == null){
            //查最大等级
            grade = sysDataMapDao.queryMaxGrade();
        }
        return grade;
    }
}
