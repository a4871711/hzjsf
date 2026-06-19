package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.GymTypeMapper;
import com.dlc.modules.api.entity.GymType;
import com.dlc.modules.sys.dao.SysGymEngineDao;
import com.dlc.modules.sys.entity.GymEngineEntity;
import com.dlc.modules.sys.service.SysGymEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("gymEngineService")
public class SysSysGymEngineServiceImpl implements SysGymEngineService {
    @Autowired
    private SysGymEngineDao sysGymEngineDao;
    @Autowired
    private GymTypeMapper gymTypeMapper;

    @Override
    public GymEngineEntity queryObject(Long gymEngineId) {
        return sysGymEngineDao.queryObject(gymEngineId);
    }

    @Override
    public List<GymEngineEntity> queryList(Map<String, Object> map) {
        return sysGymEngineDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysGymEngineDao.queryTotal(map);
    }

    @Override
    public void save(GymEngineEntity gymEngineEntity) {
        sysGymEngineDao.save(gymEngineEntity);
    }

    @Override
    public void update(GymEngineEntity gymEngineEntity) {
        sysGymEngineDao.update(gymEngineEntity);
    }

    @Override
    public void deleteBatch(Long[] gymEngineIds) {
        sysGymEngineDao.deleteBatch(gymEngineIds);
    }

    @Override
    public List<GymType> queryGymTypeList(Query query) {
        return gymTypeMapper.queryGymTypeList(query);
    }

    @Override
    public int queryGymTypeTotal(Query query) {
        return gymTypeMapper.queryGymTypeTotal(query);
    }

    @Override
    public int deleteGymType(Long gtId) {
        return gymTypeMapper.deleteByPrimaryKey(gtId);
    }

    @Override
    public List<GymType> getGymTypeList() {
        return gymTypeMapper.selectAllGymType();
    }

    @Override
    public int saveGymType(GymType gymType) {
        return gymTypeMapper.insertSelective(gymType);
    }

    @Override
    public int updateGymType(GymType gymType) {
        return gymTypeMapper.updateByPrimaryKey(gymType);
    }

}
