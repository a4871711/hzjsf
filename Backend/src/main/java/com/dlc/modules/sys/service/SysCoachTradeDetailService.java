package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysCoachTradeDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 教练收支明细
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-28 10:37:48
 */
public interface SysCoachTradeDetailService {

    SysCoachTradeDetailEntity queryObject(Long id);

    List<SysCoachTradeDetailEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysCoachTradeDetailEntity sysCoachTradeDetailEntity);

    void update(SysCoachTradeDetailEntity sysCoachTradeDetailEntity);

    void deleteBatch(Long[] ids);

}

