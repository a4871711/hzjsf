package com.dlc.modules.api.service;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
public interface CoachAppointmentService {
    List<Map<String,Object>> reducibleTime(Long coachId);

    String queryCoachAppointment(Long coachId, String dataTime);

    int updateCoachAppointment(Long coachId, String dataTime, String dataJsonArray);
}
