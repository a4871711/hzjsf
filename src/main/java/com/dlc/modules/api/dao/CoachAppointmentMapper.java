package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.CoachAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CoachAppointmentMapper {
    int deleteByPrimaryKey(Long appointId);

    int insert(CoachAppointment record);

    int insertSelective(CoachAppointment record);

    CoachAppointment selectByPrimaryKey(Long appointId);

    int updateByPrimaryKeySelective(CoachAppointment record);

    int updateByPrimaryKey(CoachAppointment record);

    List<Map<String,Object>> reducibleTime(Long coachId);

    String queryCoachAppointment(Map<String,Object> map);

    int updateCoachAppointment(Map<String, Object> map);

    int saveCoachAppointment(Map<String, Object> map);

    CoachAppointment selectByAppTimeAndCoachId(Map<String,Object> params);
}