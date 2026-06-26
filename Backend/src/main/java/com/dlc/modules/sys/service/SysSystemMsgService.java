package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysSystemMsgEntity;

import java.util.List;
import java.util.Map;

/**
 * 系统消息记录表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-10 17:46:22
 */
public interface SysSystemMsgService {

    SysSystemMsgEntity queryObject(Long id);

    List<SysSystemMsgEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysSystemMsgEntity sysSystemMsgEntity);

    void update(SysSystemMsgEntity sysSystemMsgEntity);

    void deleteBatch(Long[] ids);

}

