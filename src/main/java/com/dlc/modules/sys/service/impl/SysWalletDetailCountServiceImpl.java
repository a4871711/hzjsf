package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysWalletDetailCountDao;
import com.dlc.modules.sys.entity.MessageEntity;
import com.dlc.modules.sys.service.SysWalletDetailCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.dlc.modules.sys.entity.SysWalletDetailEntity;
import java.util.List;

@Service("SysWalletDetailCountService")
public class SysWalletDetailCountServiceImpl  implements SysWalletDetailCountService {

    @Autowired
    private SysWalletDetailCountDao sysWalletDetailCountDao;

    @Override
    public SysWalletDetailEntity queryObject(Long id) {
        return sysWalletDetailCountDao.queryObject(id);
    }

    @Override
    public List<SysWalletDetailEntity> queryList(Map<String, Object> map) {
        return sysWalletDetailCountDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysWalletDetailCountDao.queryTotal(map);
    }

    @Override
    public void save(SysWalletDetailEntity sysWalletDetailEntity) {
        sysWalletDetailCountDao.save(sysWalletDetailEntity);
    }

    @Override
    public void update(SysWalletDetailEntity sysWalletDetailEntity) {
        sysWalletDetailCountDao.update(sysWalletDetailEntity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysWalletDetailCountDao.deleteBatch(ids);
    }

    @Override
    public List<MessageEntity> selectMsg() {
        return sysWalletDetailCountDao.selectMsg();
    }


}
