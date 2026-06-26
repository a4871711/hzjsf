package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysStoreCoachEntity;

import java.util.List;
import java.util.Map;

/**
 * 场馆教练
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-04-01 15:52:42
 */
public interface SysStoreCoachService {

    SysStoreCoachEntity queryObject(Long id);

    List<SysStoreCoachEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    int save(SysStoreCoachEntity sysStoreCoachEntity);

    int update(SysStoreCoachEntity sysStoreCoachEntity);

    int deleteBatch(Long[] ids);

}

