package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.modules.api.entity.PtCoachOption;
import com.dlc.modules.api.vo.PtAvailableSlotVo;

import java.util.List;
import java.util.Map;

/**
 * 私教预约全链路 Service(第14步,M3 头号并发风险点)。
 * 容量护栏:路线A(主)=预约事务内锁同教练同日行(SELECT..FOR UPDATE 间隙锁串行化)后 COUNT 判余量;
 * 路线B(加固,仅一对一)=slot_key+uk_pt_appt_slot 唯一键兜底,撞键转 ERROR_SLOT_TAKEN。
 * 列表余量与下单护栏共用 PtPrivateAppointmentDao.countOccupied 同一条 SQL 口径。
 * 课时账本联动第11步 MemberPrivateBenefitService:预约=freeze / 核销=finish / 取消=cancel(同事务)。
 *
 * @author claude
 */
public interface PrivateAppointmentService {

    /** 本人某权益可约教练:校验权益属本人且生效中,按权益商品走 8.3 交集(复用第10步 listByProduct) */
    List<PtCoachOption> coaches(Long memberId, Long benefitId);

    /**
     * 某教练某日可约时段:排班窗口按 duration+gap 切片,过滤 latest_booking_hours 内过近时段,
     * 余量 = 容量(一对一恒1) - countOccupied,仅返回余量>0 的时段。storeId 可为 null(不限门店)。
     */
    List<PtAvailableSlotVo> slots(Long memberId, Long benefitId, Long coachId, Long storeId, String date);

    /**
     * 会员预约(单事务):权益属本人/生效中/未过期/剩余>=1 + 时段在启用排班内且切片合法(ERROR_SLOT_ILLEGAL)
     * + 距开课满足 latest_booking_hours → 路线A锁行判余量(满则 ERROR_SLOT_TAKEN) → INSERT 占位(路线B slot_key)
     * → freeze(benefitId,1)。返回 {appointmentId, appointmentNo}。
     */
    Map<String, Object> book(Long memberId, Long benefitId, Long coachId, Long storeId,
                             String date, String startTime, String endTime);

    /**
     * 会员取消:仅本人、仅 status=1;距开课不足 latest_free_cancel_hours 抛 ERROR_CANCEL_WINDOW
     * (一期口径:取消窗口外直接拒绝,超窗由后台/爽约任务处理,详细文档 4.3)。
     * 条件 UPDATE 1→2 幂等 + 清 slot_key + cancel(benefitId,n)。
     */
    void cancelByMember(Long memberId, String appointmentNo, String cancelReason);

    /** 本人预约分页列表,params 需含 userId/page/limit,可选 status */
    PageUtils myList(Map<String, Object> params);

    /* ==================== 以下供 sys 后台(第15步)委托调用,api 端不暴露 ==================== */

    /**
     * 完成核销(后台):条件 UPDATE 1→3 + finish_at + finish(benefitId,n);
     * 已完成幂等返回不重复扣,已取消/爽约报错。
     */
    void finishAppointment(Long appointmentId, Long operatorId);

    /**
     * 后台取消(不受 latest_free_cancel_hours 窗口限制):条件 UPDATE 1→2 幂等 + 清 slot_key + cancel 解冻。
     */
    void cancelByAdmin(Long appointmentId, String cancelReason, Long operatorId);

    /**
     * 教练代约(后台):校验教练只能约自己门店会员(会员 nowStoreId ∈ 教练所属门店),
     * 自动取该会员该商品先到期的可用权益,复用 book 主流程,created_by=管理员ID。
     */
    Map<String, Object> coachBook(Long coachId, Long memberId, Long productId,
                                  String date, String startTime, String endTime, Long operatorId);

    /**
     * 单条爽约处理(第16步 PtAppointmentNoShowTask 逐条调用,一条一个 REQUIRED 事务):
     * 条件 UPDATE 1→4 爽约(幂等,0行=已被人工处理则静默返回),再按商品 no_show_deduct 快照
     * 决定账本走向——=1 扣课(frozen→used,复用第11步 finish)、=0 不扣(frozen→remaining,复用 cancel),
     * 与人工完成/取消共用同一账本口径,恒等式不破。
     */
    void markNoShow(Long appointmentId);
}
