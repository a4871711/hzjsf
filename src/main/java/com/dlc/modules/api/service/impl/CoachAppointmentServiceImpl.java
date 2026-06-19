package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.DateUtils;
import com.dlc.modules.api.dao.CoachAppointmentMapper;
import com.dlc.modules.api.service.CoachAppointmentService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
@Service
@Transactional
public class CoachAppointmentServiceImpl implements CoachAppointmentService{
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private CoachAppointmentMapper coachAppointmentMapper;
    @Override
    public List<Map<String, Object>> reducibleTime(Long coachId) {

        return coachAppointmentMapper.reducibleTime(coachId);
    }

    @Override
    public String queryCoachAppointment(Long coachId, String dataTime) {
        log.info("queryCoachAppointment:{}"+coachId+"**"+dataTime);
        Map<String,Object> map = new HashMap<>();
        map.put("coachId",coachId);
        if(StringUtils.isBlank(dataTime)){
            dataTime = DateUtils.format(new Date());
        }
        map.put("appTime",dataTime);
        /*if (dataTime!=null) {
            return coachAppointmentMapper.queryCoachAppointment(map);
        }
        String nowDataTime = DateUtils.format(new Date());
        map.put("dataTime",nowDataTime);*/
        return coachAppointmentMapper.queryCoachAppointment(map);
    }

    @Override
    public int updateCoachAppointment(Long coachId, String dataTime, String dataJsonArray) {
        Map<String,Object> map = new HashMap<>();
        map.put("coachId",coachId);
        map.put("appTime",dataTime);
        String data = coachAppointmentMapper.queryCoachAppointment(map);
        if (data!=null){
            //更新
            map.put("appDate",dataJsonArray);
            return coachAppointmentMapper.updateCoachAppointment(map);
        }
        map.put("appDate",dataJsonArray);
        return coachAppointmentMapper.saveCoachAppointment(map);
    }
}
