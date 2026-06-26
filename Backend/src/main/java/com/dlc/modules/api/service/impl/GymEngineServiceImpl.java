package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.GymEngineMapper;
import com.dlc.modules.api.dao.GymTypeMapper;
import com.dlc.modules.api.service.GymEngineService;
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
public class GymEngineServiceImpl implements GymEngineService{

    @Autowired
    private GymEngineMapper gymEngineMapper;
    @Autowired
    private GymTypeMapper gymTypeMapper;
    /**
     *  @Auther:YD
     *  @parameters:
     *  查询器械列表
     */
    @Override
    public List<Map<String, Object>> queryGymEngineList(Map<String,Object> params) {
        List<Map<String, Object>> oneList = gymTypeMapper.queryOneTypeList(params);
        return oneList;
    }

    @Override
    public int queryTotal(Map<String,Object> params) {
        //return gymEngineMapper.queryTotal(params);
        return gymTypeMapper.queryOneTypeTotal(params);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  器械详情查询
     */
    @Override
    public Map<String, Object> queryGymEngineInfo(Long id) {
        Map<String,Object> map = gymEngineMapper.queryGymEngineInfo(id);
        return map;
    }

    @Override
    public List<Map<String, Object>> querySecondGymEngineList(Query query) {
        return gymEngineMapper.queryGymEngineListByGtId(query);
    }

    @Override
    public int querySecondTotal(Query query) {
        return gymEngineMapper.queryGymEngineTotalByGtId(query);
    }
}
