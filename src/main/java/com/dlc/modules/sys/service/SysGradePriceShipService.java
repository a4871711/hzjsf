package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysGradePriceShipEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SysGradePriceShipService {
    //查询所有
    List<SysGradePriceShipEntity> queryList(Map<String, Object> map);
    SysGradePriceShipEntity queryObject(Long id);
    //新增
    int save(SysGradePriceShipEntity entity);
    //修改
    int update(SysGradePriceShipEntity entity);

    //总数
    int queryTotal(Map<String, Object> map);
    void delete(Long id);

    void deleteBatch(Long[] ids);
    void savap(Long entityId,Long dengji,BigDecimal price);

    Integer getGradeRule(Double money);
}
