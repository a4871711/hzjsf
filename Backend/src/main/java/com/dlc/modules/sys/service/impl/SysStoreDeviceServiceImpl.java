package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.service.SysStoreDeviceService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.dlc.modules.sys.dao.SysStoreDeviceDao;
import com.dlc.modules.sys.entity.SysStoreDeviceEntity;
import java.util.List;

@Service("storeDeviceService")
public class SysStoreDeviceServiceImpl  implements SysStoreDeviceService {

    @Autowired
    private SysStoreDeviceDao sysStoreDeviceDao;

    @Override
    public SysStoreDeviceEntity queryObject(Long id) {
        return sysStoreDeviceDao.queryObject(id);
    }

    @Override
    public List<SysStoreDeviceEntity> queryList(Map<String, Object> map) {
        /*Long sId = ShiroUtils.getUserEntity().getStoreId();
        map.put("storeId",sId);*/
        return sysStoreDeviceDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysStoreDeviceDao.queryTotal(map);
    }

    @Override
    public void save(SysStoreDeviceEntity sysStoreDeviceEntity) {
        Long sId = ShiroUtils.getUserEntity().getStoreId();
        sysStoreDeviceEntity.setStoreId(sId);

        sysStoreDeviceDao.save(sysStoreDeviceEntity);
    }

    @Override
    public void update(SysStoreDeviceEntity sysStoreDeviceEntity) {
        sysStoreDeviceDao.update(sysStoreDeviceEntity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysStoreDeviceDao.deleteBatch(ids);
    }

    @Override
    public int queryExistSameDevice(String deviceNo) {
        return sysStoreDeviceDao.queryExistSameDevice(deviceNo);
    }

    @Override
    public int querySameDevice(SysStoreDeviceEntity storeDeviceEntity) {
        return sysStoreDeviceDao.querySameDevice(storeDeviceEntity);
    }


}
