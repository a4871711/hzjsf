package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.ActivityTypeMapper;
import com.dlc.modules.api.entity.ActivityType;
import com.dlc.modules.sys.dao.SysActivityTrainDao;
import com.dlc.modules.sys.entity.SysActivityTrainEntity;
import com.dlc.modules.sys.service.SysActivityTrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/***
 * 动作训练
 */
@Service
public class SysActivityTrainServiceImpl implements SysActivityTrainService {
    @Autowired
    private SysActivityTrainDao sysActivityTrainDao;
    @Autowired
    private ActivityTypeMapper activityTypeMapper;
    @Override
    public SysActivityTrainEntity queryObject(Long activityTrainId) {
        return sysActivityTrainDao.queryObject(activityTrainId);
    }

    @Override
    public List<SysActivityTrainEntity> queryList(Map<String, Object> map) {
        return sysActivityTrainDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysActivityTrainDao.queryTotal(map);
    }

    @Override
    public void save(SysActivityTrainEntity activityTrainEntity) {
        sysActivityTrainDao.save(activityTrainEntity);
    }

    @Override
    public void update(SysActivityTrainEntity activityTrainEntity) {
        sysActivityTrainDao.update(activityTrainEntity);
    }

    @Override
    public void delete(Long activityTrainId) {
        sysActivityTrainDao.delete(activityTrainId);
    }

    @Override
    public void deleteBatch(Long[] activityTrainIds) {
        sysActivityTrainDao.deleteBatch(activityTrainIds);
    }

    @Override
    public List<ActivityType> queryActivList(Query query) {
        return activityTypeMapper.queryActivTypeList(query);
    }

    @Override
    public int queryActivTotal(Query query) {
        return activityTypeMapper.queryActivTypeTotal(query);
    }

    @Override
    public List<ActivityType> selectAllActivType() {
        return activityTypeMapper.queryActivTypeAll();
    }

    @Override
    public int deleteActiveType(Long atId) {
        return activityTypeMapper.deleteByPrimaryKey(atId);
    }

    @Override
    public int saveActivityType(ActivityType activityType) {
        return activityTypeMapper.insertSelective(activityType);
    }

    @Override
    public int updateActivityType(ActivityType activityType) {
        return activityTypeMapper.updateByPrimaryKey(activityType);
    }
}
