package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysShareEntity;

import java.util.List;
import java.util.Map;

/**
 * 分享表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-13 17:53:50
 */
public interface SysShareService {

    SysShareEntity queryObject(Long id);

    List<SysShareEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysShareEntity sysShareEntity);

    void update(SysShareEntity sysShareEntity);

    void deleteBatch(Long[] ids);

}

