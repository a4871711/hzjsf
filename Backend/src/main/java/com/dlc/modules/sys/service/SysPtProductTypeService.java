package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtProductTypeEntity;

import java.util.List;
import java.util.Map;

/**
 * 私教商品类型 Service
 *
 * @author claude
 */
public interface SysPtProductTypeService {

    PtProductTypeEntity queryObject(Long id);

    List<PtProductTypeEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(PtProductTypeEntity entity);

    void update(PtProductTypeEntity entity);

    void deleteBatch(Long[] ids);

    void updateStatus(Long id, Integer status);

    List<PtProductTypeEntity> queryOptions();
}
