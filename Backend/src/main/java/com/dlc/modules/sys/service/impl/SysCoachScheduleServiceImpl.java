package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.PtCoachScheduleDao;
import com.dlc.modules.sys.entity.PtCoachScheduleEntity;
import com.dlc.modules.sys.service.SysCoachScheduleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 教练固定周排班 Service 实现。事务由 sys.service.impl 切面统一管理。
 *
 * @author claude
 */
@Service("sysCoachScheduleService")
public class SysCoachScheduleServiceImpl implements SysCoachScheduleService {

    private static final String[] WEEK_CN = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Autowired
    private PtCoachScheduleDao ptCoachScheduleDao;

    @Override
    public PtCoachScheduleEntity queryObject(Long id) {
        return ptCoachScheduleDao.queryObject(id);
    }

    @Override
    public List<PtCoachScheduleEntity> queryList(Map<String, Object> map) {
        return ptCoachScheduleDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return ptCoachScheduleDao.queryTotal(map);
    }

    @Override
    public void save(PtCoachScheduleEntity entity) {
        if (entity.getCoachId() == null) {
            throw new RRException("请选择教练");
        }
        if (entity.getWeekdays() == null || entity.getWeekdays().isEmpty()) {
            throw new RRException("请至少选择一个星期");
        }
        if (entity.getStoreIds() == null || entity.getStoreIds().isEmpty()) {
            throw new RRException("请至少选择一个可预约门店");
        }
        validateTime(entity.getStartTime(), entity.getEndTime());
        Date now = new Date();
        for (Long storeId : entity.getStoreIds()) {
            if (storeId == null) { continue; }
            if (ptCoachScheduleDao.countCoachStore(entity.getCoachId(), storeId) == 0) {
                throw new RRException("所选门店不属于该教练的所属门店");
            }
            for (Integer weekday : entity.getWeekdays()) {
                if (weekday == null) { continue; }
                if (ptCoachScheduleDao.countOverlap(entity.getCoachId(), storeId, weekday,
                        entity.getStartTime(), entity.getEndTime(), null) > 0) {
                    throw new RRException("排班时间重叠：" + weekCn(weekday) + " " + entity.getStartTime() + "-" + entity.getEndTime());
                }
                PtCoachScheduleEntity row = new PtCoachScheduleEntity();
                row.setCoachId(entity.getCoachId());
                row.setStoreId(storeId);
                row.setWeekday(weekday);
                row.setStartTime(entity.getStartTime());
                row.setEndTime(entity.getEndTime());
                row.setIsEnabled(entity.getIsEnabled() == null ? 1 : entity.getIsEnabled());
                row.setCreatedBy(entity.getCreatedBy());
                row.setUpdatedBy(entity.getCreatedBy());
                row.setCreatedAt(now);
                row.setUpdatedAt(now);
                ptCoachScheduleDao.save(row);
            }
        }
    }

    @Override
    public void update(PtCoachScheduleEntity entity) {
        PtCoachScheduleEntity old = ptCoachScheduleDao.queryObject(entity.getId());
        if (old == null) {
            throw new RRException("排班不存在");
        }
        // TODO 第14步回填：该排班段被未来预约占用时，禁止修改关键时间(星期/起止/门店)，仅允许改启用状态。
        //      pt_private_appointment 未建前先放行关键时间修改。
        Long coachId = old.getCoachId();
        Long storeId = entity.getStoreId() != null ? entity.getStoreId() : old.getStoreId();
        Integer weekday = entity.getWeekday() != null ? entity.getWeekday() : old.getWeekday();
        String startTime = entity.getStartTime() != null ? entity.getStartTime() : old.getStartTime();
        String endTime = entity.getEndTime() != null ? entity.getEndTime() : old.getEndTime();
        validateTime(startTime, endTime);
        if (entity.getStoreId() != null && ptCoachScheduleDao.countCoachStore(coachId, storeId) == 0) {
            throw new RRException("所选门店不属于该教练的所属门店");
        }
        if (ptCoachScheduleDao.countOverlap(coachId, storeId, weekday, startTime, endTime, entity.getId()) > 0) {
            throw new RRException("排班时间重叠：" + weekCn(weekday) + " " + startTime + "-" + endTime);
        }
        entity.setUpdatedAt(new Date());
        ptCoachScheduleDao.update(entity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        // TODO 第14步回填：被未来预约占用的排班段不可删除（pt_private_appointment 未建前先放行）。
        ptCoachScheduleDao.deleteBatch(ids);
    }

    @Override
    public void changeEnabled(Long id, Integer isEnabled) {
        PtCoachScheduleEntity u = new PtCoachScheduleEntity();
        u.setId(id);
        u.setIsEnabled(isEnabled);
        u.setUpdatedAt(new Date());
        ptCoachScheduleDao.update(u);
    }

    private void validateTime(String startTime, String endTime) {
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            throw new RRException("请填写开始和结束时间");
        }
        // HH:mm 零填充格式下字符串比较等价于时间比较
        if (endTime.compareTo(startTime) <= 0) {
            throw new RRException("结束时间必须晚于开始时间");
        }
    }

    private String weekCn(Integer weekday) {
        if (weekday != null && weekday >= 1 && weekday <= 7) {
            return WEEK_CN[weekday];
        }
        return "周" + weekday;
    }
}
