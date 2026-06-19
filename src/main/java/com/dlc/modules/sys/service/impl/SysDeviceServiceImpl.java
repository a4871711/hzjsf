package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysDeviceDao;
import com.dlc.modules.sys.entity.SysDeviceEntity;
import com.dlc.modules.sys.service.SysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SysDeviceServiceImpl implements SysDeviceService {

    @Autowired
    private SysDeviceDao sysDeviceDao;

    @Override
    public SysDeviceEntity queryObject(Long deviceId) {
        return sysDeviceDao.queryObject(deviceId);
    }

    @Override
    public List<SysDeviceEntity> queryList(Map<String, Object> map) {
        return sysDeviceDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysDeviceDao.queryTotal(map);
    }

    @Override
    public void save(SysDeviceEntity sysDeviceEntity) {
        try {
            int res = sysDeviceDao.save(sysDeviceEntity);
            if(res > 0){
                //更新用户表手环id
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put("userId", sysDeviceEntity.getProxyId());
                objectMap.put("phone", sysDeviceEntity.getPhone().trim());
                objectMap.put("deviceNo", sysDeviceEntity.getDeviceNo().trim());
                sysDeviceDao.updateUserInfoWrist(objectMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int update(SysDeviceEntity sysDeviceEntity) {
        try {
            //int res = sysDeviceDao.update(sysDeviceEntity);
            sysDeviceEntity.setStatus(1);//确认已绑定
            //更新用户表手环id
            Map<String, Object> objectMap = new HashMap<String, Object>();
            objectMap.put("userId", sysDeviceEntity.getProxyId());
            objectMap.put("phone", sysDeviceEntity.getPhone().trim());
            objectMap.put("deviceNo", sysDeviceEntity.getDeviceNo().trim());
            int res = sysDeviceDao.updateUserInfoWrist(objectMap);
            if(res > 0){
                sysDeviceDao.updateDevice(sysDeviceEntity);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void deleteBatch(Long[] deviceIds) {
        sysDeviceDao.deleteBatch(deviceIds);
    }

    @Override
    public int batchForbidden(Long[] deviceIds) {
        return sysDeviceDao.batchForbidden(deviceIds);
    }

    @Override
    public int batchUnForbidden(Long[] deviceIds) {
        return sysDeviceDao.batchUnForbidden(deviceIds);
    }

    @Override
    public int queryIfExistDeciceNo(SysDeviceEntity device) {
        return sysDeviceDao.queryIfExistDeciceNo(device);
    }

    @Override
    public int updateDeviceInit(String deviceNo) {
        int res = sysDeviceDao.updateDeviceInit(deviceNo);
        if(res > 0){
            sysDeviceDao.updateWristIdInit(deviceNo);
        }
        return res;
    }
}
