package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysGoodsEvaluateEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品评价表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-30 13:13:07
 */
public interface SysGoodsEvaluateService {

    SysGoodsEvaluateEntity queryObject(Long id);

    List<SysGoodsEvaluateEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysGoodsEvaluateEntity sysGoodsEvaluateEntity);

    void update(SysGoodsEvaluateEntity sysGoodsEvaluateEntity);

    void deleteBatch(Long[] ids);

}

