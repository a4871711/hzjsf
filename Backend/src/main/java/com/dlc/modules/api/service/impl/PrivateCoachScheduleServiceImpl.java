package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.CoachApiDao;
import com.dlc.modules.api.service.PrivateCoachScheduleService;
import com.dlc.modules.sys.entity.PtCoachScheduleEntity;
import com.dlc.modules.sys.service.SysCoachScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 教练端固定周排班实现。
 * 实际增删改复用后台 SysCoachScheduleService，保证两端校验与落库口径一致。
 */
@Service("privateCoachScheduleService")
public class PrivateCoachScheduleServiceImpl implements PrivateCoachScheduleService {

    @Autowired
    private CoachApiDao coachApiDao;
    @Autowired
    private SysCoachScheduleService sysCoachScheduleService;

    @Override
    public Map<String, Object> list(Long userId) {
        Map<String, Object> coach = requireCoach(userId);
        Long coachId = numberToLong(coach.get("id"));
        Map<String, Object> query = new HashMap<>();
        query.put("coachId", coachId);

        Map<String, Object> result = new HashMap<>();
        result.put("coach", coach);
        result.put("stores", coachApiDao.queryCoachStores(coachId));
        result.put("schedules", sysCoachScheduleService.queryList(query));
        return result;
    }

    @Override
    public void save(Long userId, Long storeId, Integer weekday, String startTime,
                     String endTime, Integer isEnabled) {
        if (storeId == null) {
            throw new RRException("请选择门店");
        }
        if (isEnabled != null && isEnabled != 0 && isEnabled != 1) {
            throw new RRException("排班启用状态非法");
        }
        Long coachId = boundCoachId(userId);
        PtCoachScheduleEntity entity = new PtCoachScheduleEntity();
        entity.setCoachId(coachId);
        entity.setStoreIds(Collections.singletonList(storeId));
        entity.setWeekdays(Collections.singletonList(weekday));
        entity.setStartTime(trim(startTime));
        entity.setEndTime(trim(endTime));
        entity.setIsEnabled(isEnabled == null ? 1 : isEnabled);
        entity.setCreatedBy(userId);
        sysCoachScheduleService.save(entity);
    }

    @Override
    public void update(Long userId, Long id, Long storeId, Integer weekday,
                       String startTime, String endTime) {
        PtCoachScheduleEntity old = requireOwnedSchedule(userId, id);
        PtCoachScheduleEntity entity = new PtCoachScheduleEntity();
        entity.setId(old.getId());
        entity.setStoreId(storeId);
        entity.setWeekday(weekday);
        entity.setStartTime(trim(startTime));
        entity.setEndTime(trim(endTime));
        entity.setUpdatedBy(userId);
        sysCoachScheduleService.update(entity);
    }

    @Override
    public void changeEnabled(Long userId, Long id, Integer isEnabled) {
        requireOwnedSchedule(userId, id);
        sysCoachScheduleService.changeEnabled(id, isEnabled);
    }

    @Override
    public void delete(Long userId, Long id) {
        PtCoachScheduleEntity owned = requireOwnedSchedule(userId, id);
        sysCoachScheduleService.deleteBatch(new Long[]{owned.getId()});
    }

    /** 每次写操作都重新核验排班归属，防止客户端越权操作其他教练的 id。 */
    private PtCoachScheduleEntity requireOwnedSchedule(Long userId, Long id) {
        if (id == null) {
            throw new RRException("缺少参数 id");
        }
        Long coachId = boundCoachId(userId);
        PtCoachScheduleEntity schedule = sysCoachScheduleService.queryObject(id);
        if (schedule == null || !coachId.equals(schedule.getCoachId())) {
            throw new RRException("排班不存在或无权操作");
        }
        return schedule;
    }

    private Long boundCoachId(Long userId) {
        return numberToLong(requireCoach(userId).get("id"));
    }

    private Map<String, Object> requireCoach(Long userId) {
        Map<String, Object> coach = coachApiDao.queryBoundCoachByUserId(userId);
        if (coach == null) {
            throw new RRException("当前账号未绑定正常状态的私教");
        }
        return coach;
    }

    private Long numberToLong(Object value) {
        if (!(value instanceof Number)) {
            throw new RRException("教练身份数据异常");
        }
        return ((Number) value).longValue();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
