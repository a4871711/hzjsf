package com.dlc.modules.api.service;

import java.util.Map;

/** 教练端固定周排班业务。 */
public interface PrivateCoachScheduleService {

    Map<String, Object> list(Long userId);

    void save(Long userId, Long storeId, Integer weekday, String startTime,
              String endTime, Integer isEnabled);

    void update(Long userId, Long id, Long storeId, Integer weekday,
                String startTime, String endTime);

    void changeEnabled(Long userId, Long id, Integer isEnabled);

    void delete(Long userId, Long id);
}
