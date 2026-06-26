package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysStoreCoachEvaluateEntity;

import java.util.List;
import java.util.Map;

/**
 * 门店教练评价表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-04-02 09:09:30
 */
public interface SysStoreCoachEvaluateService {

    SysStoreCoachEvaluateEntity queryObject(Long id);

    List<SysStoreCoachEvaluateEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    int save(SysStoreCoachEvaluateEntity sysStoreCoachEvaluateEntity);

    int update(SysStoreCoachEvaluateEntity sysStoreCoachEvaluateEntity);

    int deleteBatch(Long[] ids);

}

