package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
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
    /** 跨模块注入 api dao(现有惯例):排班段被未来预约占用的护栏 */
    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;

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
                validateWeekday(weekday);
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
        // 第14步回填：该排班段被未来预约占用时，禁止修改关键时间(星期/起止/门店)，仅允许改启用状态。
        // 占用判定按旧排班段口径(既有预约都是在旧窗口内切片约成的)。
        if (isKeyFieldChanged(entity, old) && countFutureOccupied(old) > 0) {
            throw new RRException("该排班段已被未来预约占用，不可修改星期/时间/门店，仅可调整启用状态");
        }
        Long coachId = old.getCoachId();
        Long storeId = entity.getStoreId() != null ? entity.getStoreId() : old.getStoreId();
        Integer weekday = entity.getWeekday() != null ? entity.getWeekday() : old.getWeekday();
        String startTime = entity.getStartTime() != null ? entity.getStartTime() : old.getStartTime();
        String endTime = entity.getEndTime() != null ? entity.getEndTime() : old.getEndTime();
        validateTime(startTime, endTime);
        validateWeekday(weekday);
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
        // 第14步回填：被未来预约(status=1 且未开课)占用的排班段不可删除。
        for (Long id : ids) {
            PtCoachScheduleEntity sch = ptCoachScheduleDao.queryObject(id);
            if (sch != null && countFutureOccupied(sch) > 0) {
                throw new RRException("排班段[" + weekCn(sch.getWeekday()) + " " + sch.getStartTime()
                        + "-" + sch.getEndTime() + "]已被未来预约占用，不可删除");
            }
        }
        ptCoachScheduleDao.deleteBatch(ids);
    }

    /** 关键字段(门店/星期/起止时间)是否有实际变更;仅提交且与旧值不同才算改 */
    private boolean isKeyFieldChanged(PtCoachScheduleEntity entity, PtCoachScheduleEntity old) {
        if (entity.getStoreId() != null && !entity.getStoreId().equals(old.getStoreId())) {
            return true;
        }
        if (entity.getWeekday() != null && !entity.getWeekday().equals(old.getWeekday())) {
            return true;
        }
        if (entity.getStartTime() != null && !entity.getStartTime().equals(old.getStartTime())) {
            return true;
        }
        if (entity.getEndTime() != null && !entity.getEndTime().equals(old.getEndTime())) {
            return true;
        }
        return false;
    }

    /** 落在该排班段内的未来未取消预约数(口径见 PtPrivateAppointmentDao.countFutureBySchedule) */
    private int countFutureOccupied(PtCoachScheduleEntity sch) {
        return ptPrivateAppointmentDao.countFutureBySchedule(sch.getCoachId(), sch.getStoreId(),
                sch.getWeekday(), sch.getStartTime(), sch.getEndTime());
    }

    @Override
    public void changeEnabled(Long id, Integer isEnabled) {
        if (id == null || (isEnabled == null || (isEnabled != 0 && isEnabled != 1))) {
            throw new RRException("排班启用状态非法");
        }
        PtCoachScheduleEntity old = ptCoachScheduleDao.queryObject(id);
        if (old == null) {
            throw new RRException("排班不存在");
        }
        // 停用段可能与其他启用段重叠；重新启用前必须再次执行重叠校验。
        if (isEnabled == 1 && ptCoachScheduleDao.countOverlap(old.getCoachId(), old.getStoreId(),
                old.getWeekday(), old.getStartTime(), old.getEndTime(), old.getId()) > 0) {
            throw new RRException("该时间段与现有启用排班重叠，无法启用");
        }
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
        // 前端支持分钟级自定义时间，接口仍只接受规范的 24 小时制 HH:mm，避免脏值进入排班表。
        if (!startTime.matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$")
                || !endTime.matches("^(?:[01]\\d|2[0-3]):[0-5]\\d$")) {
            throw new RRException("时间格式必须为HH:mm");
        }
        // HH:mm 零填充格式下字符串比较等价于时间比较
        if (endTime.compareTo(startTime) <= 0) {
            throw new RRException("结束时间必须晚于开始时间");
        }
    }

    private void validateWeekday(Integer weekday) {
        if (weekday == null || weekday < 1 || weekday > 7) {
            throw new RRException("星期参数必须在1到7之间");
        }
    }

    private String weekCn(Integer weekday) {
        if (weekday != null && weekday >= 1 && weekday <= 7) {
            return WEEK_CN[weekday];
        }
        return "周" + weekday;
    }
}
