package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.CoachApiDao;
import com.dlc.modules.api.dao.PtMemberPrivateBenefitDao;
import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.PtCoachOption;
import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.api.entity.PtProduct;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.CoachApiService;
import com.dlc.modules.api.service.MemberPrivateBenefitService;
import com.dlc.modules.api.service.PrivateAppointmentService;
import com.dlc.modules.api.vo.PtAvailableSlotVo;
import com.dlc.modules.api.vo.PtScheduleWindowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 私教预约全链路实现。落在 api.service.impl,命中事务切面(REQUIRED),一个业务方法一个事务。
 * 并发口径(与详细文档 5.1/5.2 一致):
 * - 列表余量与下单护栏共用 ptPrivateAppointmentDao.countOccupied 一条 SQL,杜绝"看得见约不上";
 * - 路线A(主):book 事务内 lockCoachDay(FOR UPDATE 间隙锁)串行化同教练同日并发后再 COUNT 判满;
 *   idx_..._coach_time 是普通索引拦不了双约,故行锁不可省;
 * - 路线B(加固,仅一对一):INSERT 带 slot_key,撞 uk_pt_appt_slot 捕获 DuplicateKeyException 转 ERROR_SLOT_TAKEN。
 * 锁序约定:先锁预约表(教练日行) → 再锁权益行(freeze/cancel/finish 内部),全链路同序防死锁。
 */
@Service("privateAppointmentService")
public class PrivateAppointmentServiceImpl implements PrivateAppointmentService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;
    @Autowired
    private PtMemberPrivateBenefitDao ptMemberPrivateBenefitDao;
    @Autowired
    private MemberPrivateBenefitService memberPrivateBenefitService;
    @Autowired
    private CoachApiService coachApiService;
    @Autowired
    private CoachApiDao coachApiDao;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public List<PtCoachOption> coaches(Long memberId, Long benefitId) {
        PtMemberPrivateBenefitEntity benefit = checkOwnBenefit(memberId, benefitId);
        // 复用第10步 8.3 交集:商品指定教练 ∩ 门店 ∩ status=1 ∩ 有启用排班
        return coachApiService.listByProduct(benefit.getProductId());
    }

    @Override
    public List<PtAvailableSlotVo> slots(Long memberId, Long benefitId, Long coachId, Long storeId, String date) {
        PtMemberPrivateBenefitEntity benefit = checkOwnBenefit(memberId, benefitId);
        PtProduct product = loadProductRule(benefit.getProductId());
        LocalDate targetDate = parseDate(date);
        List<PtScheduleWindowVo> windows = ptPrivateAppointmentDao.querySchedules(
                product.getId(), coachId, targetDate.getDayOfWeek().getValue(), storeId);
        List<PtAvailableSlotVo> result = new ArrayList<>();
        int cap = capacityOf(product);
        LocalDateTime cutoff = LocalDateTime.now().plusHours(latestBookingHoursOf(product));
        for (SlotPoint sp : sliceWindows(windows, product, targetDate, cutoff)) {
            // 【统一口径】与 book 护栏共用 countOccupied
            int used = ptPrivateAppointmentDao.countOccupied(coachId, date,
                    sp.startTime.format(TIME_FMT), sp.endTime.format(TIME_FMT));
            int remain = cap - used;
            if (remain > 0) {
                PtAvailableSlotVo vo = new PtAvailableSlotVo();
                vo.setStoreId(sp.storeId);
                vo.setDate(date);
                vo.setStartTime(sp.startTime.format(TIME_FMT));
                vo.setEndTime(sp.endTime.format(TIME_FMT));
                vo.setCapacity(cap);
                vo.setRemaining(remain);
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> book(Long memberId, Long benefitId, Long coachId, Long storeId,
                                    String date, String startTime, String endTime) {
        PtMemberPrivateBenefitEntity benefit = checkOwnBenefit(memberId, benefitId);
        return doBook(benefit, coachId, storeId, date, startTime, endTime, memberId);
    }

    @Override
    public void cancelByMember(Long memberId, String appointmentNo, String cancelReason) {
        if (memberId == null || appointmentNo == null || appointmentNo.trim().isEmpty()) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        PtPrivateAppointmentEntity apt = ptPrivateAppointmentDao.selectByNo(appointmentNo.trim());
        // 越权(非本人)按不存在返回,不暴露他人预约
        if (apt == null || !memberId.equals(apt.getMemberId())) {
            throw new RRException("预约不存在");
        }
        if (Integer.valueOf(2).equals(apt.getAppointmentStatus())) {
            return; // 已取消,幂等成功
        }
        if (!Integer.valueOf(1).equals(apt.getAppointmentStatus())) {
            throw new RRException("该预约已完成或已处理，不可取消");
        }
        // 无责取消窗口:距开课 >= latest_free_cancel_hours;一期口径超窗直接拒绝(详细文档 4.3)
        PtProduct product = loadProductRule(apt.getProductId());
        int freeCancelHours = product.getLatestFreeCancelHours() == null ? 2 : product.getLatestFreeCancelHours();
        LocalDateTime classStart = LocalDateTime.of(parseDate(apt.getAppointmentDate()),
                parseTime(apt.getStartTime()));
        if (LocalDateTime.now().plusHours(freeCancelHours).isAfter(classStart)) {
            throw new RRException(CodeAndMsg.ERROR_CANCEL_WINDOW);
        }
        doCancel(apt, cancelReason);
    }

    @Override
    public PageUtils myList(Map<String, Object> params) {
        Query query = new Query(params);
        List<PtPrivateAppointmentEntity> list = ptPrivateAppointmentDao.queryMyList(query);
        int total = ptPrivateAppointmentDao.countMyList(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public void finishAppointment(Long appointmentId, Long operatorId) {
        if (appointmentId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        PtPrivateAppointmentEntity apt = ptPrivateAppointmentDao.queryObject(appointmentId);
        if (apt == null) {
            throw new RRException("预约不存在");
        }
        if (Integer.valueOf(3).equals(apt.getAppointmentStatus())) {
            return; // 已完成,幂等成功不重复扣
        }
        // 条件 UPDATE 1→3 幂等闸:0行=已被并发处理或状态不允许
        if (ptPrivateAppointmentDao.finishAppointment(appointmentId, operatorId) == 0) {
            PtPrivateAppointmentEntity again = ptPrivateAppointmentDao.queryObject(appointmentId);
            if (again != null && Integer.valueOf(3).equals(again.getAppointmentStatus())) {
                return; // 并发下已被别人核销,幂等成功
            }
            throw new RRException("仅已预约状态可完成核销");
        }
        // 冻结转已用(第11步,同事务)
        memberPrivateBenefitService.finish(apt.getBenefitId(), lessonOf(apt));
    }

    @Override
    public void cancelByAdmin(Long appointmentId, String cancelReason, Long operatorId) {
        if (appointmentId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        PtPrivateAppointmentEntity apt = ptPrivateAppointmentDao.queryObject(appointmentId);
        if (apt == null) {
            throw new RRException("预约不存在");
        }
        if (Integer.valueOf(2).equals(apt.getAppointmentStatus())) {
            return; // 已取消,幂等成功
        }
        if (!Integer.valueOf(1).equals(apt.getAppointmentStatus())) {
            throw new RRException("该预约已完成或已处理，不可取消");
        }
        // 后台取消不受无责窗口限制(窗口只约束会员自助取消)
        doCancel(apt, cancelReason);
    }

    @Override
    public Map<String, Object> coachBook(Long coachId, Long memberId, Long productId,
                                         String date, String startTime, String endTime, Long operatorId) {
        if (coachId == null || memberId == null || productId == null || operatorId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        UserInfo member = userInfoMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            throw new RRException("会员不存在");
        }
        // 教练只能约自己门店的会员:会员当前门店须 ∈ 教练所属门店
        long memberStoreId = member.getNowStoreId();
        if (memberStoreId <= 0) {
            throw new RRException("该会员未绑定门店，无法代约");
        }
        List<Long> coachStoreIds = coachApiDao.queryCoachStoreIds(coachId);
        if (!coachStoreIds.contains(memberStoreId)) {
            throw new RRException("教练只能为自己门店的会员代约");
        }
        // 自动取该会员该商品先到期的可用权益
        PtMemberPrivateBenefitEntity benefit = ptPrivateAppointmentDao.selectUsableBenefit(memberId, productId);
        if (benefit == null) {
            throw new RRException(CodeAndMsg.ERROR_LESSON_NOT_ENOUGH);
        }
        // 上课门店=会员当前门店,复用会员自约主流程;created_by=管理员ID 标记代约来源
        return doBook(benefit, coachId, memberStoreId, date, startTime, endTime, operatorId);
    }

    @Override
    public void markNoShow(Long appointmentId) {
        if (appointmentId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        PtPrivateAppointmentEntity apt = ptPrivateAppointmentDao.queryObject(appointmentId);
        // 不存在或已非"已预约"(被人工核销/取消/上轮已置爽约):幂等静默返回,不动账本
        if (apt == null || !Integer.valueOf(1).equals(apt.getAppointmentStatus())) {
            return;
        }
        // no_show_deduct 快照取商品规则行(与预约/取消规则同源);null 按 DDL 默认 1 扣课
        PtProduct product = loadProductRule(apt.getProductId());
        boolean deduct = product.getNoShowDeduct() == null
                || Integer.valueOf(1).equals(product.getNoShowDeduct());
        // 条件 UPDATE 1→4 幂等闸:与人工完成(1→3)/取消(1→2)互斥,0行=已被并发处理,幂等返回
        if (ptPrivateAppointmentDao.markNoShow(appointmentId) == 0) {
            return;
        }
        // 账本联动(第11步,同事务):扣课=等价 finish(frozen→used),不扣=等价 cancel(frozen→remaining)
        if (deduct) {
            memberPrivateBenefitService.finish(apt.getBenefitId(), lessonOf(apt));
        } else {
            memberPrivateBenefitService.cancel(apt.getBenefitId(), lessonOf(apt));
        }
    }

    /* ==================== 私有主流程 ==================== */

    /**
     * 预约主流程(单事务):时段合法性 → 路线A行锁 → 同一口径 COUNT 判余量 → INSERT 占位(路线B) → freeze。
     */
    private Map<String, Object> doBook(PtMemberPrivateBenefitEntity benefit, Long coachId, Long storeId,
                                       String date, String startTime, String endTime, Long createdBy) {
        if (coachId == null || storeId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 剩余课时前置校验(freeze 锁内还会复核,这里给出更早更准的错误码)
        if (benefit.getRemainingLessons() == null || benefit.getRemainingLessons() < 1) {
            throw new RRException(CodeAndMsg.ERROR_LESSON_NOT_ENOUGH);
        }
        PtProduct product = loadProductRule(benefit.getProductId());
        LocalDate targetDate = parseDate(date);
        LocalTime reqStart = parseTime(startTime);
        LocalTime reqEnd = parseTime(endTime);

        // 时段必须命中"启用排班窗口按 duration+gap 切出的合法切片"(含门店归属),否则 ERROR_SLOT_ILLEGAL
        List<PtScheduleWindowVo> windows = ptPrivateAppointmentDao.querySchedules(
                product.getId(), coachId, targetDate.getDayOfWeek().getValue(), storeId);
        boolean legal = false;
        for (SlotPoint sp : sliceWindows(windows, product, targetDate, null)) {
            if (sp.startTime.equals(reqStart) && sp.endTime.equals(reqEnd) && sp.storeId.equals(storeId)) {
                legal = true;
                break;
            }
        }
        if (!legal) {
            throw new RRException(CodeAndMsg.ERROR_SLOT_ILLEGAL);
        }
        // 距开课不足 latest_booking_hours 不可约(与 slots 列表过滤同参数)
        LocalDateTime classStart = LocalDateTime.of(targetDate, reqStart);
        if (LocalDateTime.now().plusHours(latestBookingHoursOf(product)).isAfter(classStart)) {
            throw new RRException(CodeAndMsg.ERROR_SLOT_ILLEGAL);
        }

        String normStart = reqStart.format(TIME_FMT);
        String normEnd = reqEnd.format(TIME_FMT);

        // 路线A(主):锁同教练同日行,串行化后 COUNT 才可信;间隙锁保证"当日无记录"时也能拦并发插入
        ptPrivateAppointmentDao.lockCoachDay(coachId, date);
        int cap = capacityOf(product);
        // 【统一口径】与 slots 列表共用 countOccupied
        int used = ptPrivateAppointmentDao.countOccupied(coachId, date, normStart, normEnd);
        if (used >= cap) {
            throw new RRException(CodeAndMsg.ERROR_SLOT_TAKEN);
        }
        // 同会员同时段(跨教练)拦重复约
        if (ptPrivateAppointmentDao.countMemberOverlap(benefit.getMemberId(), date, normStart, normEnd) > 0) {
            throw new RRException("该时段已有您的预约，请勿重复预约");
        }

        PtPrivateAppointmentEntity apt = new PtPrivateAppointmentEntity();
        apt.setAppointmentNo(genAppointmentNo());
        apt.setOrderId(benefit.getOrderId());
        apt.setBenefitId(benefit.getId());
        apt.setMemberId(benefit.getMemberId());
        apt.setProductId(benefit.getProductId());
        apt.setCoachId(coachId);
        apt.setStoreId(storeId);
        apt.setAppointmentDate(date);
        apt.setStartTime(normStart);
        apt.setEndTime(normEnd);
        apt.setLessonCount(1);
        apt.setAppointmentStatus(1);
        apt.setCreatedBy(createdBy);
        apt.setUpdatedBy(createdBy);
        // 路线B(加固,仅一对一):slot_key=coachId:date:startTime,uk_pt_appt_slot 兜底;一对多置 NULL
        if (Integer.valueOf(1).equals(product.getServiceType())) {
            apt.setSlotKey(coachId + ":" + date + ":" + normStart);
        }
        try {
            ptPrivateAppointmentDao.save(apt);
        } catch (DuplicateKeyException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("uk_pt_appt_slot")) {
                // 路线B兜底命中:并发第二单撞唯一键=时段已被占
                throw new RRException(CodeAndMsg.ERROR_SLOT_TAKEN);
            }
            // 撞的是 appointment_no 唯一键:换号重试一次(全局单号约定)
            apt.setAppointmentNo(genAppointmentNo());
            ptPrivateAppointmentDao.save(apt);
        }
        // 冻结课时(第11步,同事务;权益行锁在预约表锁之后取得,全链路同序)
        memberPrivateBenefitService.freeze(benefit.getId(), 1);

        Map<String, Object> result = new HashMap<>();
        result.put("appointmentId", apt.getId());
        result.put("appointmentNo", apt.getAppointmentNo());
        return result;
    }

    /** 取消落库 + 解冻(会员/后台共用;窗口校验由调用方前置) */
    private void doCancel(PtPrivateAppointmentEntity apt, String cancelReason) {
        // 条件 UPDATE 1→2 幂等闸 + 清 slot_key 释放一对一占位
        if (ptPrivateAppointmentDao.cancelAppointment(apt.getId(), cancelReason) == 0) {
            PtPrivateAppointmentEntity again = ptPrivateAppointmentDao.queryObject(apt.getId());
            if (again != null && Integer.valueOf(2).equals(again.getAppointmentStatus())) {
                return; // 并发下已被取消,幂等成功
            }
            throw new RRException("该预约已完成或已处理，不可取消");
        }
        // 冻结回剩余(第11步,同事务)
        memberPrivateBenefitService.cancel(apt.getBenefitId(), lessonOf(apt));
    }

    /** 权益归属与状态校验:属本人、status=1 生效中、未过期 */
    private PtMemberPrivateBenefitEntity checkOwnBenefit(Long memberId, Long benefitId) {
        if (memberId == null || benefitId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        PtMemberPrivateBenefitEntity benefit = ptMemberPrivateBenefitDao.queryObject(benefitId);
        if (benefit == null || !memberId.equals(benefit.getMemberId())) {
            // 越权按不存在返回
            throw new RRException(CodeAndMsg.ERROR_BENEFIT_NOT_EXIST);
        }
        if (!Integer.valueOf(1).equals(benefit.getStatus())) {
            throw new RRException(CodeAndMsg.ERROR_BENEFIT_STATUS_ABNORMAL);
        }
        if (benefit.getExpireAt() != null && !benefit.getExpireAt().after(new java.util.Date())) {
            throw new RRException(CodeAndMsg.ERROR_BENEFIT_EXPIRED);
        }
        return benefit;
    }

    /** 商品预约规则行(不过滤上架状态:下架不影响已购权益) */
    private PtProduct loadProductRule(Long productId) {
        PtProduct product = ptPrivateAppointmentDao.selectProductRule(productId);
        if (product == null) {
            throw new RRException(CodeAndMsg.ERROR_PRODUCT_NOT_EXIST);
        }
        return product;
    }

    /** 容量口径:一对一恒1,一对多取 booking_capacity(空按1) */
    private int capacityOf(PtProduct product) {
        if (Integer.valueOf(1).equals(product.getServiceType())) {
            return 1;
        }
        return product.getBookingCapacity() == null ? 1 : product.getBookingCapacity();
    }

    private int latestBookingHoursOf(PtProduct product) {
        return product.getLatestBookingHours() == null ? 2 : product.getLatestBookingHours();
    }

    private int lessonOf(PtPrivateAppointmentEntity apt) {
        return apt.getLessonCount() == null ? 1 : apt.getLessonCount();
    }

    /**
     * 排班窗口切片(5.4):cursor 从窗口起点,每片 duration 分钟,片间隔 gap 分钟;
     * cutoff 非空时过滤开课时间早于 cutoff 的时段(slots 列表用),book 合法性校验传 null 不过滤,
     * 时间前置校验单独做,保证"切片合法"与"预约过近"两种错误可区分。
     */
    private List<SlotPoint> sliceWindows(List<PtScheduleWindowVo> windows, PtProduct product,
                                         LocalDate targetDate, LocalDateTime cutoff) {
        int duration = product.getDurationMinutes() == null ? 60 : product.getDurationMinutes();
        int gap = product.getBookingGapMinutes() == null ? 0 : product.getBookingGapMinutes();
        List<SlotPoint> points = new ArrayList<>();
        for (PtScheduleWindowVo w : windows) {
            LocalTime cursor = parseTime(w.getStartTime());
            LocalTime windowEnd = parseTime(w.getEndTime());
            while (!cursor.plusMinutes(duration).isAfter(windowEnd)) {
                LocalTime slotStart = cursor;
                LocalTime slotEnd = cursor.plusMinutes(duration);
                if (cutoff == null || !LocalDateTime.of(targetDate, slotStart).isBefore(cutoff)) {
                    points.add(new SlotPoint(w.getStoreId(), slotStart, slotEnd));
                }
                cursor = slotEnd.plusMinutes(gap);
            }
        }
        return points;
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FMT);
        } catch (Exception e) {
            throw new RRException("日期格式不合法，应为 yyyy-MM-dd");
        }
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time, TIME_FMT);
        } catch (Exception e) {
            throw new RRException("时间格式不合法，应为 HH:mm");
        }
    }

    /** 预约编号:PA + yyyyMMddHHmmss + 随机(全局单号约定 §0.6.2) */
    private String genAppointmentNo() {
        return "PA" + OrderNoGenerator.getOrderIdByTime();
    }

    /** 切片点(门店+起止时间),仅内部计算用 */
    private static class SlotPoint {
        final Long storeId;
        final LocalTime startTime;
        final LocalTime endTime;

        SlotPoint(Long storeId, LocalTime startTime, LocalTime endTime) {
            this.storeId = storeId;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}
