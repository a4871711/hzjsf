package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtProductCategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 私教商品分类 Service。
 */
public interface SysPtProductCategoryService {

    PtProductCategoryEntity queryObject(Long id);

    List<PtProductCategoryEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(PtProductCategoryEntity entity);

    void update(PtProductCategoryEntity entity);

    void deleteBatch(Long[] ids);

    void updateStatus(Long id, Integer status);

    List<PtProductCategoryEntity> queryOptions();
}
