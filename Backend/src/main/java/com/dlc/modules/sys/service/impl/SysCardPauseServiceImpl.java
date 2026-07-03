package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysCardPauseDao;
import com.dlc.modules.sys.service.SysCardPauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 停卡记录后台服务实现(只读列表)。
 *
 * @date 2026-07-03
 */
@Service("sysCardPauseService")
public class SysCardPauseServiceImpl implements SysCardPauseService {

    @Autowired
    private SysCardPauseDao sysCardPauseDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysCardPauseDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysCardPauseDao.queryTotal(params);
    }
}
