package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.CoachApiDao;
import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
import com.dlc.modules.api.dao.PtProductApiDao;
import com.dlc.modules.api.entity.PtCoachOption;
import com.dlc.modules.api.entity.PtProduct;
import com.dlc.modules.api.service.CoachApiService;
import com.dlc.modules.api.vo.PtAvailableSlotVo;
import com.dlc.modules.api.vo.PtScheduleWindowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 私教教练会员端只读浏览 Service 实现。
 *
 * @author claude
 */
@Service("coachApiService")
public class CoachApiServiceImpl implements CoachApiService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private CoachApiDao coachApiDao;
    @Autowired
    private PtProductApiDao ptProductApiDao;
    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;

    @Override
    public List<PtCoachOption> listByProduct(Long productId) {
        PtProduct product = ptProductApiDao.queryObject(productId);
        if (product == null) {
            throw new RRException("商品不存在或已下架");
        }
        return coachApiDao.queryBookableCoaches(productId);
    }

    @Override
    public List<PtAvailableSlotVo> availableSlots(Long productId, Long coachId, Long storeId, String date) {
        PtProduct product = ptProductApiDao.queryObject(productId);
        if (product == null) {
            throw new RRException("商品不存在或已下架");
        }
        List<Long> productStoreIds = coachApiDao.queryProductStoreIds(productId);
        if (!productStoreIds.contains(storeId)) {
            throw new RRException("该门店不在商品适用门店范围内");
        }
        List<Long> coachStoreIds = coachApiDao.queryCoachStoreIds(coachId);
        if (!coachStoreIds.contains(storeId)) {
            throw new RRException("该教练不属于所选门店");
        }
        boolean coachBookable = false;
        for (PtCoachOption c : coachApiDao.queryBookableCoaches(productId)) {
            if (c.getId().equals(coachId)) {
                coachBookable = true;
                break;
            }
        }
        if (!coachBookable) {
            throw new RRException("该教练当前不可预约此商品");
        }

        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(date, DATE_FMT);
        } catch (Exception e) {
            throw new RRException("日期格式不合法，应为 yyyy-MM-dd");
        }
        // 星期：1周一...7周日，与 DayOfWeek.getValue() 定义一致
        int weekday = targetDate.getDayOfWeek().getValue();
        List<PtScheduleWindowVo> windows = coachApiDao.queryEnabledSchedule(coachId, storeId, weekday);

        int duration = product.getDurationMinutes() == null ? 60 : product.getDurationMinutes();
        int gap = product.getBookingGapMinutes() == null ? 0 : product.getBookingGapMinutes();
        // 容量口径(第14步统一):一对一恒1,一对多取 booking_capacity
        int capacity = Integer.valueOf(1).equals(product.getServiceType()) ? 1
                : (product.getBookingCapacity() == null ? 1 : product.getBookingCapacity());
        int latestBookingHours = product.getLatestBookingHours() == null ? 2 : product.getLatestBookingHours();
        LocalDateTime cutoff = LocalDateTime.now().plusHours(latestBookingHours);

        List<PtAvailableSlotVo> slots = new ArrayList<>();
        for (PtScheduleWindowVo w : windows) {
            LocalTime cursor = LocalTime.parse(w.getStartTime(), TIME_FMT);
            LocalTime windowEnd = LocalTime.parse(w.getEndTime(), TIME_FMT);
            while (!cursor.plusMinutes(duration).isAfter(windowEnd)) {
                LocalTime slotStart = cursor;
                LocalTime slotEnd = cursor.plusMinutes(duration);
                LocalDateTime slotStartDateTime = LocalDateTime.of(targetDate, slotStart);
                if (!slotStartDateTime.isBefore(cutoff)) {
                    // 【统一余量口径,第14步接入】与预约下单护栏共用 countOccupied 同一条 COUNT SQL,
                    // 已占用=状态 IN (1已预约,3已完成,4爽约),排除已取消;仅返回余量>0 的时段(5.1.B)
                    int used = ptPrivateAppointmentDao.countOccupied(coachId, date,
                            slotStart.format(TIME_FMT), slotEnd.format(TIME_FMT));
                    int remaining = capacity - used;
                    if (remaining > 0) {
                        PtAvailableSlotVo vo = new PtAvailableSlotVo();
                        vo.setStoreId(storeId);
                        vo.setDate(date);
                        vo.setStartTime(slotStart.format(TIME_FMT));
                        vo.setEndTime(slotEnd.format(TIME_FMT));
                        vo.setCapacity(capacity);
                        vo.setRemaining(remaining);
                        slots.add(vo);
                    }
                }
                cursor = slotEnd.plusMinutes(gap);
            }
        }
        return slots;
    }
}
