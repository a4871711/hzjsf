package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.ActivityTrainMapper;
import com.dlc.modules.api.dao.ActivityTypeMapper;
import com.dlc.modules.api.service.ActivityTrainService;

import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/10/010
 */
@Service
@Transactional
public class ActivityTrainServiceImpl implements ActivityTrainService{
    @Autowired
    private ActivityTrainMapper activityTrainMapper;
    @Autowired
    private ActivityTypeMapper activityTypeMapper;

    /**
     *  @Auther:YD
     *  @parameters:
     *  动作训练列表
     */
    @Override
    public List<Map<String, Object>> queryActivityTrainList(Map<String, Object> params) {
        List<Map<String, Object>> typeList = activityTypeMapper.queryActivityTypeList(params);
        return typeList;
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        //return activityTrainMapper.queryTotal(params);
        return activityTypeMapper.queryActivityTypeTotal(params);
    }

    @Override
    public Map<String, Object> queryActivityTrainInfo(Long id) {
        Map<String,Object> map = activityTrainMapper.queryActivityTrainInfo(id);
        return map;
    }

    @Override
    public List<Map<String, Object>> querySecondActivityTrainList(Query query) {
        return activityTrainMapper.queryActivityTrainListByAtId(query);
    }

    @Override
    public int querySecondActivityTrainTotal(Query query) {
        return activityTrainMapper.queryActivityTrainTotalByAtId(query);
    }
}
