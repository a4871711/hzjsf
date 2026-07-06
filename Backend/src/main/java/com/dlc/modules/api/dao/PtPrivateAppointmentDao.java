package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.PtMemberPrivateBenefitEntity;
import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.api.entity.PtProduct;
import com.dlc.modules.api.vo.PtScheduleWindowVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教预约 Dao(pt_private_appointment),第14步预约全链路。
 * 并发护栏口径(必须共用):
 * - countOccupied 是「列表余量(5.1.B)」与「下单护栏(5.2)」唯一的已占用 COUNT SQL,
 *   状态 IN (1已预约,3已完成,4爽约),排除2已取消;两处不允许各写各的,否则"看得见约不上"。
 * - lockCoachDay 借 idx_pt_private_appointment_coach_time 的行锁/间隙锁串行化同教练同日并发(路线A)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtPrivateAppointmentDao {

    PtPrivateAppointmentEntity queryObject(Long id);

    /** 按预约编号查(cancel 入口用,本人校验在 service 层做) */
    PtPrivateAppointmentEntity selectByNo(@Param("appointmentNo") String appointmentNo);

    /** 占位插入(回填自增 id);slot_key 撞 uk_pt_appt_slot 由调用方捕获 DuplicateKeyException 转 ERROR_SLOT_TAKEN */
    int save(PtPrivateAppointmentEntity entity);

    /**
     * 路线A主护栏:锁同教练同日全部预约行(SELECT ... FOR UPDATE)。
     * 即便当日暂无记录,也借 idx_..._coach_time 间隙锁阻塞并发插入,使随后的 countOccupied 在串行下成立。
     */
    List<Long> lockCoachDay(@Param("coachId") Long coachId, @Param("appointmentDate") String appointmentDate);

    /**
     * 【统一余量口径】该教练该日与 [startTime,endTime) 重叠的已占用预约数。
     * 余量 = 容量(一对一恒1,一对多 booking_capacity) - 本 COUNT;
     * slots 列表与 book 下单护栏、第10步 availableSlots 全部走本方法。
     */
    int countOccupied(@Param("coachId") Long coachId, @Param("appointmentDate") String appointmentDate,
                      @Param("startTime") String startTime, @Param("endTime") String endTime);

    /** 同会员同日重叠时段的进行中预约数(>0 拦"同会员同时段重复约") */
    int countMemberOverlap(@Param("memberId") Long memberId, @Param("appointmentDate") String appointmentDate,
                           @Param("startTime") String startTime, @Param("endTime") String endTime);

    /** 取消:1已预约→2已取消 + cancel_at + 置 slot_key=NULL 释放一对一占位;WHERE status=1 幂等,0行=已被处理 */
    int cancelAppointment(@Param("id") Long id, @Param("cancelReason") String cancelReason);

    /** 完成核销:1已预约→3已完成 + finish_at;WHERE status=1 幂等,0行=已被处理 */
    int finishAppointment(@Param("id") Long id, @Param("operatorId") Long operatorId);

    /** 本人预约分页列表(联表回填商品/教练/门店名) */
    List<PtPrivateAppointmentEntity> queryMyList(Query query);

    int countMyList(Query query);

    /**
     * 教练该星期对该商品可用的启用排班窗口(含门店):
     * 商品适用门店 ∩ 教练所属门店 ∩ 教练正常 ∩ 商品指定教练(为空不限) ∩ is_enabled=1,
     * 与第10步 queryBookableCoaches(CoachApiDao.xml)交集口径一致。storeId 传 null 则不限门店。
     */
    List<PtScheduleWindowVo> querySchedules(@Param("productId") Long productId, @Param("coachId") Long coachId,
                                            @Param("weekday") Integer weekday, @Param("storeId") Long storeId);

    /** 预约规则用商品行:仅 deleted=0,不过滤上架状态(已购权益不因商品下架而不可预约) */
    PtProduct selectProductRule(@Param("productId") Long productId);

    /** 代约取会员对该商品当前可用权益:status=1 未过期 remaining>=1,先到期先用 */
    PtMemberPrivateBenefitEntity selectUsableBenefit(@Param("memberId") Long memberId,
                                                     @Param("productId") Long productId);

    /** 教练最近预约(后台教练详情只读抽屉用,联表回填会员/商品/门店名),按上课时间倒序取 limit 条 */
    List<PtPrivateAppointmentEntity> queryRecentByCoach(@Param("coachId") Long coachId,
                                                        @Param("limit") Integer limit);

    /* ==================== 第5/7步护栏回填(sys 侧删除/改排班引用校验) ==================== */

    /** 教练名下未来未取消(status=1 且未开课)预约数;>0 教练不可删除 */
    int countFutureByCoach(@Param("coachId") Long coachId);

    /** 落在某排班段(教练+门店+星期+起止时间)内的未来未取消预约数;>0 排班不可改关键时间/不可删 */
    int countFutureBySchedule(@Param("coachId") Long coachId, @Param("storeId") Long storeId,
                              @Param("weekday") Integer weekday,
                              @Param("startTime") String startTime, @Param("endTime") String endTime);

    /* ==================== 第16步 定时任务:爽约扫描 ==================== */

    /** 爽约到期扫描:status=1 且预约结束时间已过 hours 小时仍未核销/取消(第16步任务用) */
    List<PtPrivateAppointmentEntity> queryNoShowDue(@Param("hours") int hours);

    /**
     * 置爽约:1已预约→4爽约+finish_at(记爽约判定时间);WHERE status=1 幂等,
     * 0行=已被人工核销/取消。slot_key 保留(时段已过且4计入 countOccupied 占用口径)。
     */
    int markNoShow(@Param("id") Long id);
}
