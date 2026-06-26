package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.CoachClassShipMapper;
import com.dlc.modules.api.entity.CoachClassShip;
import com.dlc.modules.api.service.CoachClassShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-09-18 20:10
 */
@Service
@Transactional
public class CoachClassShipServiceImpl implements CoachClassShipService {

    @Autowired
    private CoachClassShipMapper coachClassShipMapper;

    @Override
    public List<Map<String, Object>> queryCoachClass(Map<String, Object> map) {
        return coachClassShipMapper.queryCoachClass(map);
    }

    @Override
    public int queryCoachClassRecordTotal(Query query) {
        return coachClassShipMapper.queryCoachClassRecordTotal(query);
    }

    @Override
    public int updateCoachClassShio(CoachClassShip coachClassShip) {
        return coachClassShipMapper.updateByPrimaryKeySelective(coachClassShip);
    }

    @Override
    public int addCoachClassShip(List<CoachClassShip> coachClassList) {

        return coachClassShipMapper.batchCoachClassShip(coachClassList);
    }

    @Override
    public Boolean isExistClass(CoachClassShip coachClassShip) {
        Map<String,Object> res = coachClassShipMapper.isExistClass(coachClassShip);
        if (res!=null){
            return true;
        }
        return false;
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  查询某教练 所有课程关系
     */
    @Override
    public List<CoachClassShip> queryAllCoachClass(Long coachId) {

        return coachClassShipMapper.queryAllCoachClass(coachId);
    }
}
