package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.CoachApiDao;
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
        int capacity = product.getBookingCapacity() == null ? 1 : product.getBookingCapacity();
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
                    PtAvailableSlotVo vo = new PtAvailableSlotVo();
                    vo.setDate(date);
                    vo.setStartTime(slotStart.format(TIME_FMT));
                    vo.setEndTime(slotEnd.format(TIME_FMT));
                    vo.setCapacity(capacity);
                    // 交易域 pt_private_appointment 未建成前，无已占用记录，余量恒等于容量（第14步接入后统一口径）
                    vo.setRemaining(capacity);
                    slots.add(vo);
                }
                cursor = slotEnd.plusMinutes(gap);
            }
        }
        return slots;
    }
}
