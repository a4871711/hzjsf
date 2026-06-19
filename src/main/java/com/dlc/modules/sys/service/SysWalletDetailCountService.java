package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.MessageEntity;
import com.dlc.modules.sys.entity.SysWalletDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 钱包明细表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-11 15:33:55
 */
public interface SysWalletDetailCountService {

    SysWalletDetailEntity queryObject(Long id);

    List<SysWalletDetailEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysWalletDetailEntity sysWalletDetailEntity);

    void update(SysWalletDetailEntity sysWalletDetailEntity);

    void deleteBatch(Long[] ids);

    List<MessageEntity> selectMsg();
}

